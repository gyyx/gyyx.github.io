FROM ruby

WORKDIR /opt/know

COPY Gemfile Gemfile.lock ./
RUN bundle install

CMD["bundle exec jekyll serve"]
