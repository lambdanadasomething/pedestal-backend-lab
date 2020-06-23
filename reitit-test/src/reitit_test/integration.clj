(ns reitit-test.integration
  (:require [io.pedestal.http :as server]
            [reitit.pedestal :as reitit-ped]
            [reitit.http :as http]
            [reitit.ring :as ring]))

(defn interceptor [number]
  {:enter (fn [ctx] (update-in ctx [:request :number] (fnil + 0) number))})

(def routes
  ["/api"
   {:interceptors [(interceptor 1)]}

   ["/number"
    {:interceptors [(interceptor 10)]
     :get {:interceptors [(interceptor 100)]
           :handler (fn [req]
                      {:status 200
                       :body (select-keys req [:number])})}}]])

(def server-config
  (-> {::server/type :jetty
     ::server/port 3000
     ::server/join? false
     ;; no pedestal routes
     ::server/routes []}
    (server/default-interceptors)
    ;; swap the reitit router
    (reitit-ped/replace-last-interceptor
      (reitit-ped/routing-interceptor
        (http/router routes)))
    (server/dev-interceptors)))

(def myserver (server/create-server server-config))
