(ns swagger-data.sandbox
  (:require [ring.swagger.swagger2 :as rs]
            [schema.core :as s]
            [clojure.spec.alpha :as spec]
            [clojure.edn :as edn]
            [com.walmartlabs.lacinia.util :refer [attach-resolvers]]
            [com.walmartlabs.lacinia.schema :as lacschema]
            [clojure.java.io :as io]
            [com.walmartlabs.lacinia :refer [execute]]))

(rs/swagger-json {})

(s/defschema User {:id s/Str
                   :name s/Str
                   :address {:street s/Str
                             :city (s/enum :tre :hki)}})

(def basic-swagger-schema
  (rs/swagger-json 
   {:info {:title "Sausages" :version "0.0.1"}
    :tags [{:name "user"
            :description "User stuff"}]
    :paths {"/api/ping" {:get {}}
            "/user/:id/" {:post {:summary "Get User by ID"
                                 :description "Retrieve an activated user by ID. Return a concise profile by default. Specify detail=true to return the full profile instead."
                                 :tag ["user"]
                                 :parameters {:path {:id s/Str}
                                              :body User}
                                 :responses {200 {:schema User}
                                             404 {:description "Not found"}}}}}})
  )

(s/with-fn-validation basic-swagger-schema)

(spec/conform even? 1000)

(defn ranged-rand
  "Returns random int in range start <= rand < end"
  [start end]
  (+ start (long (rand (- end start)))))

(spec/fdef ranged-rand
  :args (spec/and (spec/cat :start int? :end int?)
               #(< (:start %) (:end %)))
  :ret int?
  :fn (spec/and #(>= (:ret %) (-> % :args :start))
             #(< (:ret %) (-> % :args :end))))

(ranged-rand "hi" 19)

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

(get-hero {} {:episode :NEWHOPE} {})

(def star-wars-schema
  (-> "resources/lacinia-schema.edn"
      slurp
      edn/read-string
      (attach-resolvers {:get-hero get-hero
                         :get-droid (constantly {})})
      lacschema/compile))

(def basic-graphql-query "{ hero { id name }}")
(def variant-graphql-query "{ hero(episode: NEWHOPE) { movies: appears_in }}")
(execute star-wars-schema variant-graphql-query nil nil)
