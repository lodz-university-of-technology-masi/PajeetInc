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

		try {
			Axios.post('https://unyfv0eps9.execute-api.us-east-1.amazonaws.com/dev/signIn',
			{
				["email"]: this.state.email,
				["password"]: this.state.password
			}).then(res => {
				console.log(res)
				console.log('User logged in');
				localStorage.setItem('currentUser', JSON.stringify(res.data));
				this.props.setCurrentUser(res.data)
				this.props.userHasAuthenticated(true);
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
