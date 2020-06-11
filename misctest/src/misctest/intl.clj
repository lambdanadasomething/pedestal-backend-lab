(ns misctest.intl
  (:require [taoensso.tower :as tower :refer (with-tscope)]))

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

