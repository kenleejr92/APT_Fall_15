__author__ = 'kenlee'

from google.appengine.ext import ndb

class Stream(ndb.Model):
    owner_id = ndb.StringProperty()
    name = ndb.StringProperty()
    photos = ndb.BlobKeyProperty(repeated=True)
    num_photos = ndb.IntegerProperty()
    views = ndb.IntegerProperty()
    subscribed_users = ndb.StringProperty(repeated=True)
    view_queue = ndb.DateTimeProperty(repeated=True)
    timestamp = ndb.DateTimeProperty()
    date_last_added = ndb.DateProperty()