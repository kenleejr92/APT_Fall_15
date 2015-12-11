__author__ = 'kenlee'

import webapp2
import cgi
import json
from Handlers.UserData import User

class SearchNearbyHandler(webapp2.RequestHandler):
    def post(self):
        search_results = {'names':[],'phone_numbers':[],'emails':[]}
        nearby_string = cgi.escape(self.request.get('nearby_string'))
        query_string = cgi.escape(self.request.get('query_string'))
        nearby_ids = nearby_string.split()
        users = User.query(User.id.IN(nearby_ids))
        for user in users:
            if query_string in user.name or query_string in user.phone_number or query_string in user.email:
                search_results['names'].append(user.name)
                search_results['phone_numbers'].append(user.phone_number)
                search_results['emails'].append(user.email)
        test = {'ids':nearby_ids}
        test_json = json.dumps(test,indent=4, separators=(',', ': '))
        name_json = json.dumps(search_results,indent=4, separators=(',', ': '))
        self.response.write(name_json)
        #self.response.write(test_json)
    def get(self):
        search_results = {'names':[],'phone_numbers':[],'emails':[]}
        nearby_string = cgi.escape(self.request.get('nearby_string'))
        query_string = cgi.escape(self.request.get('query_string'))
        nearby_ids = nearby_string.split()
        users = User.query(User.id.IN(nearby_ids))
        for user in users:
            if (query_string in user.name) or (query_string in user.phone_number) or (query_string in user.email):
                search_results['names'].append(user.name)
                search_results['phone_numbers'].append(user.phone_number)
                search_results['emails'].append(user.email)
        test = {'ids':nearby_ids}
        test_json = json.dumps(test,indent=4, separators=(',', ': '))
        name_json = json.dumps(search_results,indent=4, separators=(',', ': '))
        self.response.write(name_json)
        #self.response.write(test_json)