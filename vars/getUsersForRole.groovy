import com.michelin.cio.hudson.plugins.rolestrategy.RoleBasedAuthorizationStrategy

@NonCPS
def call(role) {
    echo "Retrieving users for ${role}..."
    def users = [:]
    def authStrategy = Jenkins.instance.getAuthorizationStrategy()
    if(authStrategy instanceof RoleBasedAuthorizationStrategy){
        def sids = authStrategy.getSIDs('globalRoles')
        return sids.join(',')
    } else {
		throw new Exception("Role Strategy Plugin not in use.  Please enable to retrieve users for a role")
    }
}