(ns misctest.muuntaja
  (:require [muuntaja.core :as m]))

(->> {:kikka 42 :hello-t {:foo "test" :bar 23}}
     (m/encode "application/json")
     slurp)

(->> "{\"HelloWorld\":42, \"tester\":\"byebye\", \"o\":{\"a\":1242, \"b\":\"2020-06-05\"}}"
     (m/decode "application/json"))

(def m2
  (m/create
   (assoc-in
    m/default-options
    [:formats "application/json" :encoder-opts]
    {:date-format "yyyy-MM-dd"})))

(->> {:value (java.util.Date.)}
     (m/encode m2 "application/json")
     slurp)
