__author__ = 'kenlee'

import cgi
from google.appengine.ext import ndb
from google.appengine.ext.webapp import blobstore_handlers
from handlers.Stream import Stream


#/upload_photo
class PhotoUploadHandler(blobstore_handlers.BlobstoreUploadHandler):
    def post(self):
        try:
            upload = self.get_uploads()[0]
            str = cgi.escape(self.request.get('submit'))
            stream_name = str.split()[2]
            blob_key = upload.key()
            stream_key = ndb.Key(Stream,stream_name)
            stream = stream_key.get()
            stream.photos.append(blob_key)
            stream.put()

            self.redirect('/view_photo/%s' % upload.key())

        except:
            self.error(500)
