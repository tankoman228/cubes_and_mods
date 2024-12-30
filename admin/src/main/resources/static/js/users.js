console.log('users import');

export let data = {
    users: [],
	users_all: [],
    showBanned: false
};

export let methods = {
    async ShowBanned() {
        await this.AllUsersFilteredByBanned(true);
    },
    async ShowUnbanned() {
        await this.AllUsersFilteredByBanned(false);
    },
    async AllUsersFilteredByBanned(banned) {
		
		this.showBanned = banned;
        try {
            const response = await fetch('http://localhost:8085/users/all', {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            const data = await response.json();
			
			this.users_all = data;
            this.users = data.filter(user => user.banned == banned);
			console.log("USR list");
			console.log(this.users);
			
        } catch (error) {
            console.error('Ошибка USR', error);
        }
    },
	async banUser(u) {
		const response = await fetch('http://localhost:8085/users/ban', {
		    method: 'POST',
		    headers: {
		        'Content-Type': 'text/plain'
		    },
			body: u.email
		});
		this.AllUsersFilteredByBanned(this.showBanned);
	},
	async pardonUser(u) {
		const response = await fetch('http://localhost:8085/users/forgive/', {
			method: 'POST',
			headers: {
			    'Content-Type': 'text/plain'
			},
			body: u.email
		});
		this.AllUsersFilteredByBanned(this.showBanned);
	}
};

export function mounted() {
    this.ShowUnbanned();
}
