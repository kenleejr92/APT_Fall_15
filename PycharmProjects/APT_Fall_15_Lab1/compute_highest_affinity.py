import urllib

#Auhor: Kenneth Lee (kl22943)
# No need to process files and manipulate strings - we will
# pass in lists (of equal length) that correspond to 
# sites views. The first list is the site visited, the second is
# the user who visited the site.

# See the test cases for more details.

def highest_affinity(site_list, user_list, time_list):
  # Returned string pair should be ordered by dictionary order
  # I.e., if the highest affinity pair is "foo" and "bar"
  # return ("bar", "foo").

  #enumerate all pairs of web pages
  #For each pair, determine how many users viewed both
  #sort the list
  user_site_table = dict() #keys are users, values are list of sites visited
  site_pairs = dict()  #keys are site_pairs and values are affinity
  count = 0
  #Construct dictionary of users and sites visited
  for user in user_list :
    user_site_table.setdefault(user,None)
    if user_site_table[user] == None :
      user_sites = []
      user_site_table[user] = user_sites
      user_site_table[user].append(site_list[count])
    elif site_list[count] not in user_site_table[user] :
      user_site_table[user].append(site_list[count])
    count = count + 1
  for user in user_site_table :
    site_list = user_site_table[user]
    site_list.sort()
    start_index = 0
    #Enumerate all pairs for each user and add them to site_pairs dictionary
    for i in range(0,len(site_list)) :
        start_index = start_index + 1
        for j in range(start_index,len(site_list)) :
            if site_list[i] != site_list[j] :
              site_pairs.setdefault((site_list[i],site_list[j]),None)
              if site_pairs[(site_list[i],site_list[j])] == None :
                site_pairs[(site_list[i],site_list[j])]=1
              else :
                site_pairs[(site_list[i],site_list[j])]+=1
  frequency_list = list()
  #Convert to list for sorting
  for site_pair in site_pairs :
    frequency_list.append((site_pairs[site_pair],site_pair))
  frequency_list.sort(reverse=True)
  return frequency_list[0][1]   #return the site_pair corresponding to most frequent affinity
