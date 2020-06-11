(ns swagger-data.sandbox
  (:require [ring.swagger.swagger2 :as rs]
            [schema.core :as s]
            [clojure.spec.alpha :as spec]))

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
