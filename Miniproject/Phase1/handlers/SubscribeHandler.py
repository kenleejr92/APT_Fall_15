__author__ = 'kenlee'

import webapp2
import jinja2
import cgi
from Stream import Stream
from google.appengine.ext import ndb
from google.appengine.api import users

class SubscribeHandler(webapp2.RequestHandler):
    def post(self):

        user = users.get_current_user()
        str = cgi.escape(self.request.get('submit'))
        stream_name = str.split()[2]
        stream_key = ndb.Key(Stream,stream_name)
        stream = stream_key.get()
        stream.subscribed_users.append(user.user_id())
        stream.put()

        self.redirect('/view_stream/?stream_name=%s' % stream_name)