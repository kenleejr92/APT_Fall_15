#!/usr/bin/python

import webapp2
import jinja2
import os

from handlers.MainPageHandler import MainPageHandler
from handlers.CreateStreamHandler import CreateStreamHandler
from handlers.PhotoUploadHandler import PhotoUploadHandler
from handlers.ViewPhotoHandler import ViewPhotoHandler
from handlers.ViewStreamHandler import ViewStreamHandler
from handlers.ManagementHandler import ManagementHandler
from handlers.CreateStreamHandler1 import CreateStreamHandler1
from handlers.SearchStreamHandler import SearchStreamHandler
from handlers.SubscribeHandler import SubscribeHandler



app = webapp2.WSGIApplication([('/', MainPageHandler),
                               ('/management',ManagementHandler),
                               ('/create_stream1',CreateStreamHandler1),
                               ('/create_stream2', CreateStreamHandler),
                               ('/upload_photo', PhotoUploadHandler),
                               ('/view_photo/([^/]+)?', ViewPhotoHandler),
                               ('/view_stream/.*', ViewStreamHandler),
                               ('/search_streams',SearchStreamHandler),
                               ('/subscribe',SubscribeHandler)
                              ], debug=True)