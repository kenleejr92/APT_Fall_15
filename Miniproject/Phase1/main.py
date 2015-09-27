#!/usr/bin/python

import webapp2
import jinja2
import os

from handlers.MainPageHandler import MainPageHandler
from handlers.CreateStreamHandler import CreateStreamHandler
from handlers.PhotoUploadHandler import PhotoUploadHandler
from handlers.ViewPhotoHandler import ViewPhotoHandler
from handlers.ViewStreamHandler import ViewStreamHandler



app = webapp2.WSGIApplication([('/', MainPageHandler),
                               ('/create_stream', CreateStreamHandler),
                               ('/upload_photo', PhotoUploadHandler),
                               ('/view_photo/([^/]+)?', ViewPhotoHandler),
                               ('/view_stream/.*', ViewStreamHandler)
                              ], debug=True)