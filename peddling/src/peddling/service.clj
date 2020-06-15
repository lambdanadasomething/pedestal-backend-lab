(ns peddling.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [ring.util.response :as ring-resp]
            [io.pedestal.interceptor :as i]
            [io.pedestal.interceptor.chain :as chain]
            [io.pedestal.interceptor.error :as err]
            [com.walmartlabs.lacinia.util :refer [attach-resolvers]]
            [com.walmartlabs.lacinia.schema :as lacschema]
            [com.walmartlabs.lacinia.pedestal2 :as p2]
            [clojure.edn :as edn]
            [clojure.set :as ss]))

; Let's try our own intercepters
(def what-a-lab
  (i/interceptor
   {:name ::what-a-lab
    :enter (fn [context]
             (println "Getting in..."))}))

(defn about-page
  [request]
  (ring-resp/response (format "Clojure %s - served from %s"
                              (clojure-version)
                              (route/url-for ::about-page))))

(defn home-page
  [request]
  (ring-resp/response "Hello World! Mad Max"))

(defn sample-api
  [req]
  (let [b (:body-params req)
        creq (:charset (:muuntaja/request req))
        cres (:charset (:muuntaja/response req))]
    {:status 200 :body {:success true :id 123 :info "Okay." :data (:foo b) 
                        :creq creq :cres cres}}))

; Lacinia Pedestal integration test
; Let's try basic stuff first
(defn get-hero [context arguments value]
  (let [{:keys [episode]} arguments]
    (if (= episode :NEWHOPE)
      {:id 1000
       :name "Luke"
       :home_planet "Tatooine"
       :appears_in ["NEWHOPE" "EMPIRE" "JEDI"]}
      {:id 2000
       :name "Lando Calrissian"
       :home_planet "Socorro"
       :appears_in ["EMPIRE" "JEDI"]})))

(def star-wars-schema
  (-> "resources/lacinia-schema.edn"
      slurp
      edn/read-string
      (attach-resolvers {:get-hero get-hero
                         :get-droid (constantly {})})
      lacschema/compile))

;; Defines "/" and "/about" routes with their associated :get handlers.
;; The interceptors defined after the verb map (e.g., {:get home-page}
;; apply to / and its children (/about).
(def common-interceptors [(body-params/body-params) http/html-body])

;; Tabular routes
(def routes (ss/union 
             #{["/" :get (conj common-interceptors `home-page)]
               ["/about" :get (conj common-interceptors `about-page)]
               ["/sample" :post [`sample-api]]
               ["/graphql/q" :post (p2/default-interceptors star-wars-schema {:foo 42} )]
               ["/graphql/ide" :get (p2/graphiql-ide-handler {:api-path "/graphql/q" :asset-path "/graphql/assets"})
                :route-name ::graphiql-ide]
               }
             (p2/graphiql-asset-routes "/graphql/assets")))

;; Map-based routes
;(def routes `{"/" {:interceptors [(body-params/body-params) http/html-body]
;                   :get home-page
;                   "/about" {:get about-page}}})

;; Terse/Vector-based routes
;(def routes
;  `[[["/" {:get home-page}
;      ^:interceptors [(body-params/body-params) http/html-body]
;      ["/about" {:get about-page}]]]])


;; Consumed by peddling.server/create-server
;; See http/default-interceptors for additional options you can configure
(def service {:env :prod
              ;; You can bring your own non-default interceptors. Make
              ;; sure you include routing and set it up right for
              ;; dev-mode. If you do, many other keys for configuring
              ;; default interceptors will be ignored.
              ;; ::http/interceptors []
              ::http/routes routes

              ;; Uncomment next line to enable CORS support, add
              ;; string(s) specifying scheme, host and port for
              ;; allowed source(s):
              ;;
              ;; "http://localhost:8080"
              ;;
              ;;::http/allowed-origins ["scheme://host:port"]

              ;; Tune the Secure Headers
              ;; and specifically the Content Security Policy appropriate to your service/application
              ;; For more information, see: https://content-security-policy.com/
              ;;   See also: https://github.com/pedestal/pedestal/issues/499
              ;;::http/secure-headers {:content-security-policy-settings {:object-src "'none'"
              ;;                                                          :script-src "'unsafe-inline' 'unsafe-eval' 'strict-dynamic' https: http:"
              ;;                                                          :frame-ancestors "'none'"}}

              ;; Root for resource interceptor that is available by default.
              ::http/resource-path "/public"

              ;; Either :jetty, :immutant or :tomcat (see comments in project.clj)
              ;;  This can also be your own chain provider/server-fn -- http://pedestal.io/reference/architecture-overview#_chain_provider
              ::http/type :jetty
              ;;::http/host "localhost"
              ::http/port 8080
              ;; Options to pass to the container (Jetty)
              ::http/container-options {:h2c? true
                                        :h2? false
                                        ;:keystore "test/hp/keystore.jks"
                                        ;:key-password "password"
                                        ;:ssl-port 8443
                                        :ssl? false
                                        ;; Alternatively, You can specify you're own Jetty HTTPConfiguration
                                        ;; via the `:io.pedestal.http.jetty/http-configuration` container option.
                                        ;:io.pedestal.http.jetty/http-configuration (org.eclipse.jetty.server.HttpConfiguration.)
                                        }})
