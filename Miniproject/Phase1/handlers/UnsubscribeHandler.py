__author__ = 'kenlee'

import webapp2
from Stream import Stream
from Stream import Photo
from google.appengine.ext import ndb
from google.appengine.api import users

class UnsubscribeHandler(webapp2.RequestHandler):
    def post(self):
        user = users.get_current_user()
        unsubscribed_streams = self.request.get_all('unsubscribed_streams')
        for stream_name in unsubscribed_streams:
            stream_key = ndb.Key(Stream,stream_name)
            stream = stream_key.get()
            stream.subscribed_users.remove(user.user_id())
            stream.put()
        self.redirect('/management')