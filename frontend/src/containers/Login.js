import React, { Component } from 'react';
import { FormGroup, FormControl, ControlLabel } from 'react-bootstrap';
import LoaderButton from '../components/LoaderButton';
import Axios from 'axios';

import './Login.css';

export default class Login extends Component {
	constructor(props) {
		super(props);

		this.state = {
			isLoading: false,
			email: '',
			password: ''
		};
	}

	parseJwt(token) {
		var base64Url = token.split('.')[1];
		var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
		var jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
			return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
		}).join(''));
	
		return JSON.parse(jsonPayload);
	}

	validateForm() {
		return this.state.email.length > 0 && this.state.password.length > 0;
	}

	handleChange = event => {
		this.setState({
			[event.target.id]: event.target.value
		});
	};

	handleSubmit = async event => {
		event.preventDefault();

		this.setState({ isLoading: true });

	//	{
	//		"email": "tcg79194@eveav.com",
	//			"password": "!Password123",
	//		"profile": "Candidate"
	//	}



		try {
			Axios.post('https://unyfv0eps9.execute-api.us-east-1.amazonaws.com/dev/signIn',
			{
				["email"]: this.state.email,
				["password"]: this.state.password
			}).then(res => {
				console.log('User logged in');
				console.log(this.parseJwt(res.data.idToken))
				localStorage.setItem('currentUser', JSON.stringify(res.data));
				localStorage.setItem('currentUsername', this.parseJwt(res.data.idToken).email);
				localStorage.setItem('profile', this.parseJwt(res.data.idToken).profile);
				this.props.setCurrentUser(res.data)
				this.props.userHasAuthenticated(true);
				this.props.setUserProfile(this.parseJwt(res.data.idToken).profile)
				this.props.history.push('/');
			}).catch(res => {
				console.log(res)
				//todo wrong password message
			})
		} catch (e) {
			alert(e.message);
			this.setState({ isLoading: false });
		}
	};

	render() {
		return (
			<div className="Login">
				<form onSubmit={this.handleSubmit}>
					<FormGroup controlId="email" bsSize="large">
						<ControlLabel>Email</ControlLabel>
						<FormControl autoFocus type="email" value={this.state.email} onChange={this.handleChange} />
					</FormGroup>
					<FormGroup controlId="password" bsSize="large">
						<ControlLabel>Password</ControlLabel>
						<FormControl value={this.state.password} onChange={this.handleChange} type="password" />
					</FormGroup>
					<a href="/resetpassword">Forgot password?</a>
					<LoaderButton
						block
						bsSize="large"
						disabled={!this.validateForm()}
						type="submit"
						isLoading={this.state.isLoading}
						text="Login"
						loadingText="Logging in…"
					/>
				</form>
			</div>
		);
	}
}
