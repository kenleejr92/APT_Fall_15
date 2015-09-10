import json
import yahoo_options_data

computedJson = yahoo_options_data.contractAsJson("aapl.dat")
expectedJson = open("aapl.json").read()
expectedJson_change = open("aapl_change.json").read()

if json.loads(computedJson) != json.loads(expectedJson_change) and json.loads(computedJson) != json.loads(expectedJson):
  print "Test failed!"
  print "Expected output:", expectedJson_change
  print "Your output:", computedJson
  assert False
else:
  print "Test passed"
