__author__ = 'kenlee'

import cgi
import datetime
import json
from datetime import timedelta
from random import randint
from google.appengine.ext import ndb
from google.appengine.api import images
from google.appengine.ext import blobstore
from google.appengine.ext.webapp import blobstore_handlers
from Stream import Stream
from Stream import Photo

def random_date(start, end):
    return start + timedelta(seconds=randint(0, int((end - start).total_seconds())))

#/upload_photo
class PhotoUploadMobileHandler(blobstore_handlers.BlobstoreUploadHandler):
    def get(self):
        upload_url = blobstore.create_upload_url('/upload_photo_mobile')
        upload_url_dict = {'upload_url':upload_url}
        upload_url_json = json.dumps(upload_url_dict,indent=4, separators=(',', ': '))
        self.response.write(upload_url_json)

    def post(self):
        try:
            upload = self.get_uploads()[0]
            stream_name = cgi.escape(self.request.get('stream_name'))
            latitude = cgi.escape(self.request.get('latitude'))
            longitude = cgi.escape(self.request.get('longitude'))
            current_date = datetime.date.today()
            blob_key = upload.key()
            stream_key = ndb.Key(Stream,stream_name)
            stream = stream_key.get()
            # rand_date=random_date(datetime.date(2015,12,1),datetime.date(2015,12,25))
            new_photo = Photo(url=images.get_serving_url(blob_key), lat=latitude, lng=longitude, date=current_date)
            stream.photos.append(new_photo)
            stream.num_photos += 1
            stream.date_last_added = datetime.date.today()
            stream.put()

        except:
            self.error(500)