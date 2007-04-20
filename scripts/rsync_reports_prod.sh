#!/usr/bin/bash
# copy script

# CHMOD:
chmod -R 775 ./reports
find ./reports -type f -exec chmod 644 {} \;

# RSYNC:
rsync -vv -e ssh --times --recursive reports/ tomcat@tools1-web:~/reports