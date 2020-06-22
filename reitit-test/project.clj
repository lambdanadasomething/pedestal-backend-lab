(defproject reitit-test "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :plugins [[reifyhealth/lein-git-down "0.3.6"]]
  :repositories [["public-github" {:url "git://github.com"}]]
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [io.pedestal/pedestal.service "0.5.8"]
                 [io.pedestal/pedestal.jetty "0.5.8"]
                 [metosin/reitit-pedestal "0.5.2"]
                 [metosin/reitit "0.5.2"]
                 [metosin/malli "10ba8b963ac419ee33729315c3defb936587447c"]]
  :main ^:skip-aot reitit-test.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {}})
