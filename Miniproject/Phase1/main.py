#!/usr/bin/python

import webapp2


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
from handlers.ViewStreamsMobileHandler import ViewStreamsMobileHandler
from handlers.ViewPhotosMobileHandler import ViewPhotosMobileHandler
from handlers.PhotoUploadMobileHandler import PhotoUploadMobileHandler
from handlers.SearchStreamMobileHandler import SearchStreamMobileHandler
from handlers.NearbyPhotoHandler import NearbyPhotoHandler
from handlers.SubscribedMobileHandler import SubscribedMobileHandler


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
                               ('/view_streams_mobile',ViewStreamsMobileHandler),
                               ('/view_photos_mobile/.*',ViewPhotosMobileHandler),
                               ('/upload_photo_mobile.*', PhotoUploadMobileHandler),
                               ('/search_streams_mobile/.*', SearchStreamMobileHandler),
                               ('/nearby_photos/.*', NearbyPhotoHandler),
                               ('/subscribed_streams_mobile/.*',SubscribedMobileHandler)
                              ], debug=True)