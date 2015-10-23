__author__ = 'kenlee'

from google.appengine.ext import ndb
class Photo(ndb.Model):
    url = ndb.StringProperty()  #photo url
    lat = ndb.FloatProperty()         #photo latitude
    lng = ndb.FloatProperty()        #photo longitude
    date = ndb.DateProperty()         #photo date

class Stream(ndb.Model):
    owner_id = ndb.StringProperty() #owner
    name = ndb.StringProperty()     #stream name

    photos = ndb.StructuredProperty(Photo, repeated=True)

    num_photos = ndb.IntegerProperty()  #number of photos in stream
    views = ndb.IntegerProperty()   #number of views of the stream
    view_queue = ndb.DateTimeProperty(repeated=True)

    subscribed_users = ndb.StringProperty(repeated=True) #list of subsribed users

    timestamp = ndb.DateTimeProperty()      #date that stream was created
    date_last_added = ndb.DateProperty() #date a photo was last added to the stream

    tags = ndb.StringProperty(repeated=True)    #list of tags
    cover_image = ndb.StringProperty()      #url to cover image

