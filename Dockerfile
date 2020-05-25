FROM gitpod/workspace-full

RUN bash -c ". /home/gitpod/.sdkman/bin/sdkman-init.sh \
             && sdk install java 11.0.7-open \
             && sdk install java 20.1.0.r11-grl"

RUN curl -o /usr/local/bin/lein -L https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein \
    && chmod a+x /usr/local/bin/lein
RUN lein
