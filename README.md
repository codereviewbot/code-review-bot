# Code Review Bot

A configurable novelty utility for notifying your co-workers whether or not code pushed to your repo meets development
standards. View the app [here.](https://codereviewbot.herokuapp.com)

## Development

### Install

```bash
$ git clone git@github.com:codereviewbot/code-review-bot.git
$ cd code-review-bot
$ lein install
```

### Set Environment Variables

Code Review Bot requires the following environment variables to work. They can be set when running the app or included
in `.lein-env` as a map. See [environ.core](https://github.com/weavejester/environ) for more details.

- `MONGODB_URI` a connection string for a mongo database (i.e. `mongodb://localhost:27017/local_db`).
- `BASE_URL` the base url for the app running locally (i.e. `http://localhost:3000`).
- `JWT_SECRET` an encryption key for encoding/decoding jwt's. This is any random string.
- `USER_AGENT` this is required for Github's API. As far as I can tell, it's value doesn't matter as long as it exists.

Set this to by-pass github OAuth:

- `AUTH_USER` a JSON string representing the logged in user, should have at least a `login` key (i.e. `{"login":"fake-user"}`)

Set this for using github OAuth:

- `OAUTH_CLIENT_ID` you must get this from [Github's developer console](https://github.com/settings/developers).
- `OAUTH_CLIENT_SET` you must get this from [Github's developer console](https://github.com/settings/developers).

### Deploy

This builds an uberjar and deploys it to [heroku.](https://dashboard.heroku.com/apps/codereviewbot)

```bash
$ ./scripts/deploy.sh
```

### Sync remote database

The shell script `./scripts/sync-db.sh` will wipe and seed a (presumably) local database with the data from a (presumably)
remote database. In order for it to work, `MONGODB_URI` and `REMOTE_MONGODB_URI` must be either set in the environment or
declared in `.lein-env`.
