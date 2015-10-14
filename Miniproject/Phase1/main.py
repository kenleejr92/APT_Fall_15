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
from handlers.SearchStreamHandler import SearchStreamHandler
from handlers.SubscribeHandler import SubscribeHandler
from handlers.ErrorHandler import ErrorHandler
from handlers.ViewAllHandler import ViewAllHandler
from handlers.UpdateTrendingHandler import UpdateTrendingHandler
from handlers.TrendingHandler import TrendingHandler
from handlers.EmailHandler import EmailHandler
from handlers.DeleteStreamsHandler import DeleteStreamsHandler
from handlers.UnsubscribeHandler import UnsubscribeHandler
from handlers.ViewMapHandler import ViewMapHandler



app = webapp2.WSGIApplication([('/', MainPageHandler),
                               ('/management',ManagementHandler),
                               ('/create_stream',CreateStreamHandler),
                               ('/upload_photo/.*', PhotoUploadHandler),
                               ('/view_photo/([^/]+)?', ViewPhotoHandler),
                               ('/view_stream/.*', ViewStreamHandler),
                               ('/search_streams',SearchStreamHandler),
                               ('/subscribe',SubscribeHandler),
                               ('/view_all',ViewAllHandler),
                               ('/error',ErrorHandler),
                               ('/update',UpdateTrendingHandler),
                               ('/trending',TrendingHandler),
                               ('/email',EmailHandler),
                               ('/delete_streams',DeleteStreamsHandler),
                               ('/unsubscribe_streams',UnsubscribeHandler),
                               ('/view_map/.*',ViewMapHandler),
                              ], debug=True)