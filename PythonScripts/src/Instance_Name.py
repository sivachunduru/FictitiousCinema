from OracleComputeCloud import OracleComputeCloud
import sys

username = sys.argv[1]
password = sys.argv[2]
authDomain = sys.argv[3]
url = sys.argv[4]

occ = OracleComputeCloud(endPointUrl=url, authenticationDomain=authDomain)
occ.login(user=username, password=password)

vms=occ.getInstances(instanceName='ALL')

vmname='/Compute-'+authDomain+'/'+username+'/cloudnative-instance/'

for vm in vms:
    if vmname in vm['name']:
        print (vm['name'])
        break;


