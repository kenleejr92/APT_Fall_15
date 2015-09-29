__author__ = 'kenlee'

import webapp2
import datetime
from Stream import Stream
from google.appengine.api import users
from google.appengine.ext import ndb

class UpdateTrendingHandler(webapp2.RequestHandler):
    def get(self):
        current_time = datetime.datetime.now()
        all_streams = Stream.query()
        for stream in all_streams:
            for date in stream.view_queue:
                minback = current_time - datetime.timedelta(hours=1)
                if date < minback:
                    stream.view_queue.remove(date)
                    stream.views = stream.views - 1
            stream.put()