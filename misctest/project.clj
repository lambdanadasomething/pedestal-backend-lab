(defproject misctest "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [tongue "0.2.9"]
                 [com.taoensso/tower "3.1.0-beta4"]
                 [metosin/muuntaja "0.6.7"]
                 [luposlip/json-schema "0.2.4"]]
  :main ^:skip-aot misctest.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {}})
