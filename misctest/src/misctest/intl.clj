(ns misctest.intl
  (:require [taoensso.tower :as tower :refer (with-tscope)]
            [tongue.core :as tongue]))

(def my-tconfig
  {:dictionary ; Map or named resource containing map
   {:en   {:example {:foo         ":en :example/foo text"
                     :foo_comment "Hello translator, please do x"
                     :bar {:baz ":en :example.bar/baz text"}
                     :greeting "Hello %s, how are you?"
                     :inline-markdown "<tag>**strong**</tag>"
                     :block-markdown* "<tag>**strong**</tag>"
                     :with-exclaim!   "<tag>**strong**</tag>"
                     :with-arguments  "Num %d = %s"
                     :greeting-alias :example/greeting
                     :baz-alias      :example.bar/baz}
           :missing  "|Missing translation: [%1$s %2$s %3$s]|"}
    :en-US {:example {:foo ":en-US :example/foo text"}}
    :de    {:example {:foo ":de :example/foo text"}}
    ;:ja "test_ja.clj" ; Import locale's map from external resource
    }
   :dev-mode? true ; Set to true for auto dictionary reloading
   :fallback-locale :de})

(def t (tower/make-t my-tconfig))

(t :en-US :example/foo)
(t :en    :example/foo)

(t :en    :example/greeting "Steve")

(t :en :example/block-markdown)

(with-tscope :example
  [(t :en :foo)
   (t :en :bar/baz)])

; Localization

(tower/fmt   :en-ZA 200       :currency)
(tower/parse :en-US "$200.00" :currency)

(tower/fmt :de-DE (java.util.Date.) :dt-long)

(tower/lsort :pl ["Warsaw" "Kraków" "Łódź" "Wrocław" "Poznań"])

(mapv #(tower/fmt-msg :de "{0,choice,0#no cats|1#one cat|1<{0,number} cats}" %)
      (range 5))

; Now come to tongue

(def dicts
  { :en { ;; simple keys
          :color "Color"
          :flower "Flower"
 
          ;; namespaced keys
          :weather/rain   "Rain"
          :weather/clouds "Clouds"
 
          ;; nested maps will be unpacked into namespaced keys
          ;; this is purely for ease of dictionary writing
          :animals { :dog "Dog"   ;; => :animals/dog
                     :cat "Cat" } ;; => :animals/cat
 
          ;; substitutions
          :welcome "Hello, {1}!"
          :between "Value must be between {1} and {2}"
          ;; For using a map
          :mail-title "{user}, {title} - Message received."
 
          ;; arbitrary functions
          :count (fn [x]
                   (cond
                     (zero? x) "No items"
                     (= 1 x)   "1 item"
                     :else     "{1} items")) ;; you can return string with substitutions
        }
 
    :en-GB { :color "colour" } ;; sublang overrides
    :tongue/fallback :en }     ;; fallback locale key
)

(def translate (tongue/build-translate dicts))

(translate :en :color)
(translate :en :animals/dog)

(translate :en :welcome "Nikita")
(translate :en :between 0 103)
(translate :en :mail-title {:user "Elizabeth" :title "New message"})

(translate :en :count 4)

(translate :en-GB :color)

(def format-number-en
  (tongue/number-formatter {:group "," :decimal "."}))

(format-number-en 9999.9)

