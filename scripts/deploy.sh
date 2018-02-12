#!/usr/bin/env bash

lein with-profile deploy do clean, uberjar
heroku deploy:jar target/code-review-bot-standalone.jar --app codereviewbot
