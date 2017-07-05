from OracleComputeCloud import OracleComputeCloud
import time
import sys

if len(sys.argv) > 1:
    print(" args: " + str(sys.argv))
    username = sys.argv[1]
    password = sys.argv[2]
    authDomain = sys.argv[3]
    url = sys.argv[4]
else:
    username = 'testuser@oracle.com'
    password = 'password'

occ = OracleComputeCloud(endPointUrl=url, authenticationDomain=authDomain)
occ.login(user=username, password=password)

vms=occ.getInstances(instanceName='ALL')
for vm in vms:
    if 'cloudnative-instance' in vm['name']:
        instance = vm['name']
        break;
        
SSHKey='/Compute-'+authDomain+'/'+username+'/cloudnative.pub'
ipReservation='/Compute-'+authDomain+'/'+username+'/cloudnative-IPReservation'
securityList='/Compute-'+authDomain+'/'+username+'/cloudnative-SecurityList'
securityApplication='/Compute-'+authDomain+'/'+username+'/cloudnative-MySQL-securityApplication'
securityRule1='/Compute-'+authDomain+'/'+username+'/cloudnative-SSHRule'
securityRule2='/Compute-'+authDomain+'/'+username+'/cloudnative-MySQLRule'
storageVolume='/Compute-'+authDomain+'/'+username+'/cloudnative-storageVolume'


print('Deleting securityRule1: ' + securityRule1)
occ.deleteResource('securityRule', securityRule1)
time.sleep(15) # sleep to allow resource to be actually deleted
print('Deleted securityRule1: ' + securityRule1)

print('Deleting securityRule2: ' + securityRule2)
occ.deleteResource('securityRule', securityRule2)
time.sleep(15) # sleep to allow resource to be actually deleted
print('Deleted securityRule2: ' + securityRule2)

print('Deleting securityApplication: ' + securityApplication)
occ.deleteResource('securityApplication', securityApplication)
time.sleep(15) # sleep to allow resource to be actually deleted
print('Deleted securityApplication: ' + securityApplication)

print('Deleting instance: ' + instance)
occ.deleteResource('instance', instance)
vm = occ.getInstances(instanceName=instance)
while len(vm) > 0:
    print('Instance is being stopped. Wait for 30 seconds before re-check.')
    time.sleep(30)
    vm = occ.getInstances(instanceName=instance)
print('Deleted instance: ' + instance)

print('Deleting securityList: ' + securityList)
occ.deleteResource('securityList', securityList)
time.sleep(15) # sleep to allow resource to be actually deleted
print('Deleted securityList: ' + securityList)

print('Deleting ipReservation: ' + ipReservation)
occ.deleteResource('ipReservation', ipReservation)
time.sleep(15) # sleep to allow resource to be actually deleted
print('Deleted ipReservation: ' + ipReservation)

print('Deleting storageVolume: ' + storageVolume)
occ.deleteResource('storageVolume', storageVolume)
time.sleep(15) # sleep to allow resource to be actually deleted
print('Deleted storageVolume: ' + storageVolume)

print('Deleting SSHKey: ' + SSHKey)
occ.deleteResource('SSHKey', SSHKey)
time.sleep(15) # sleep to allow resource to be actually deleted
print('Deleted SSHKey: ' + SSHKey)

print('Deleted Compute VM!!!')
