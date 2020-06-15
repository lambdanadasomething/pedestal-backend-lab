(ns misctest.jsonschema
  (:require [json-schema.core :as jsch]))

(def basic-schema
  {:$schema "http://json-schema.org/draft-07/schema#"
   :id "https://luposlip.com/some-schema.json"
   :title "Simple"
   :description "Just getting started"
   :type "object"
   :properties {:id {:type "number"
                     :exclusiveMinimum 0}
                :name {:type "string"}}
   :required [:id :name]})

(try
  (jsch/validate basic-schema "{ \"id\": -4}")
  (catch clojure.lang.ExceptionInfo e
    (ex-data e)))

(try
  (jsch/validate basic-schema {:id 0.001 :name "hi"})
  (catch clojure.lang.ExceptionInfo e
    (ex-data e)))
