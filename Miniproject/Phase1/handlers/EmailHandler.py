__author__ = 'kenlee'

import webapp2
import jinja2
import os
from Stream import Stream
from Stream import Photo
from google.appengine.api import mail
from google.appengine.api import users

class EmailHandler(webapp2.RequestHandler):
    def get(self):
        message = mail.EmailMessage()
        user = users.get_current_user()
        message.sender = user.email()
        message.subject = "Trending"
        message.to=user.email()
        #Get the list of streams
        streams = Stream.query().order(-Stream.views)
        i = 0
        top = []
        for stream in streams :
            if i == 3 :
                break
            top.append(stream.name)
            i = i + 1
        if len(top) == 3 :
            message.body="'Top 3 Trending Streams: %s %s %s'" % (top[0], top[1], top[2])
        else:
            message.body="'There are not 3 streams in the datastore'"
        message.send()