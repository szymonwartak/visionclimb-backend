#!/bin/bash

# SBT: start auto-reload for browser-based running: ~;container:start; container:reload /

SOURCE_DIR=/home/szymon/dev/android/backend/src/main/webapp
TARGET_DIR=/home/szymon/dev/android/PG1/assets/www

# need to be hard links so the web server picks them up
ln -sfv $TARGET_DIR/index.html $SOURCE_DIR/index.html
ln -sfv $TARGET_DIR/js/jquery.ui.map $SOURCE_DIR/js
ln -sfv $TARGET_DIR/js/camera.js $SOURCE_DIR/js/camera.js
ln -sfv $TARGET_DIR/js/climage.js $SOURCE_DIR/js/climage.js
ln -sfv $TARGET_DIR/js/jquery.mobile.js $SOURCE_DIR/js/jquery.mobile.js
ln -sfv $TARGET_DIR/js/phonegap.js $SOURCE_DIR/js/phonegap.js
ln -sfv $TARGET_DIR/js/rest.js $SOURCE_DIR/js/rest.js
ln -sfv $TARGET_DIR/js/geo.js $SOURCE_DIR/js/geo.js
ln -sfv $TARGET_DIR/js/todataurl.js $SOURCE_DIR/js/todataurl.js
ln -sfv $TARGET_DIR/js/predef.js $SOURCE_DIR/js/predef.js

