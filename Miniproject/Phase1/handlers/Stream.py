__author__ = 'kenlee'

from google.appengine.ext import ndb

class Stream(ndb.Model):
    owner_id = ndb.StringProperty() #owner
    name = ndb.StringProperty()     #stream name

    photos = ndb.BlobKeyProperty(repeated=True) #photos in stream
    #photos = ndb.StructuredProperty(Photo, repeated=true)

    num_photos = ndb.IntegerProperty()  #number of photos in stream
    views = ndb.IntegerProperty()   #number of views of the stream
    view_queue = ndb.DateTimeProperty(repeated=True)

    subscribed_users = ndb.StringProperty(repeated=True) #list of subsribed users

    timestamp = ndb.DateTimeProperty()      #date that stream was created
    date_last_added = ndb.DateProperty() #date a photo was last added to the stream

    tags = ndb.StringProperty(repeated=True)    #list of tags
    cover_image = ndb.StringProperty()      #url to cover image

class Photo(ndb.Model):
    photo_title = ndb.StringProperty()      #photo title
    photo_blob_key = ndb.BlobKeyProperty()  #photo blob key
    photo_lat = ndb.FloatProperty()         #photo latitude
    photo_long = ndb.FloatProperty()        #photo longitude