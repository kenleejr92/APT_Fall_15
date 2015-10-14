__author__ = 'kenlee'

import cgi
import datetime
from google.appengine.ext import ndb
from google.appengine.ext.webapp import blobstore_handlers
from handlers.Stream import Stream



#/upload_photo
class PhotoUploadHandler(blobstore_handlers.BlobstoreUploadHandler):
    def post(self):
        try:
            upload = self.get_uploads()[0]
            stream_name = cgi.escape(self.request.get('stream_name'))
            blob_key = upload.key()
            stream_key = ndb.Key(Stream,stream_name)
            stream = stream_key.get()
            stream.photos.append(blob_key)
            stream.num_photos += 1
            stream.date_last_added = datetime.date.today()
            stream.put()

            #encure redict happens after data store is updated (doesnt work atm)
            # jobDone = False
            # while(not jobDone):
            #     check_stream = stream_key.get()
            #     if check_stream.num_photos == updatedNum : jobDone = True

            self.redirect('/view_stream/?stream_name=%s' % stream_name)
        except:
            self.error(500)
