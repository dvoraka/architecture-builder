#!/bin/bash
#
# Prepares basic RabbitMQ structure
#

# add user guest with password guest (created by default during RabbitMQ installation)
rabbitmqctl add_user guest guest

# create virtual host prototype
rabbitmqctl add_vhost prototype

# set all permissions on prototype for user guest
rabbitmqctl set_permissions -p prototype guest ".*" ".*" ".*"


exit 0
