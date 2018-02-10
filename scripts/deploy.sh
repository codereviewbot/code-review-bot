#!/usr/bin/env bash

rm -rf resources/public/css/
lein do clean, uberjar
heroku deploy:jar target/code-review-bot-standalone.jar
