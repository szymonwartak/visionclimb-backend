#!/bin/bash

# SBT: start auto-reload for browser-based running: ~;container:start; container:reload /

SOURCE_DIR=/home/szymon/dev/android/backend/src/main/webapp
TARGET_DIR=/home/szymon/dev/android/PG1/assets/www


ln -sfv $TARGET_DIR/index.html $SOURCE_DIR/index.html
ln -sfv $TARGET_DIR/js $SOURCE_DIR

