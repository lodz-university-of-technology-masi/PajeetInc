import React, { Component } from 'react';
import { HelpBlock, FormGroup, FormControl, ControlLabel } from 'react-bootstrap';
import LoaderButton from '../components/LoaderButton';
import { Auth } from 'aws-amplify';
import Axios from 'axios';

import './Signup.css';

export default class Signup extends Component {
	constructor(props) {
		super(props);

		this.state = {
			isLoading: false,
			email: '',
			password: '',
			confirmPassword: '',
			confirmationCode: '',
			profile: '',
			newUser: null
		};
	}

	validateForm() {
		return (
			this.state.email.length > 0 &&
			this.state.password.length > 0 &&
			this.state.password === this.state.confirmPassword
		);
	}

	validateConfirmationForm() {
		return this.state.confirmationCode.length > 0;
	}

	handleChange = event => {
		this.setState({
			[event.target.id]: event.target.value
		});
	};

	handleProfileChange = event => {
		this.setState({
			profile: event.target.value
		});
	}

	handleSubmit = async event => {
		event.preventDefault();

		this.setState({ isLoading: true });

		try {
			const newUser = await Auth.signUp({
				username: this.state.email,
				password: this.state.password,
				attributes: {
					email: this.state.email,
					profile: this.state.profile,
					name: this.state.profile
				}
			});
			this.setState({
				newUser
			});
		} catch (e) {
			alert(e.message);
		}

		this.setState({ isLoading: false });
	};

	handleConfirmationSubmit = async event => {
		event.preventDefault();

		this.setState({ isLoading: true });

		try {
			await Auth.confirmSignUp(this.state.email, this.state.confirmationCode);
			console.log({
				["id"]: this.state.username,["username"]: this.state.email, ["profile"]: this.state.profile})
			Axios.post('https://unyfv0eps9.execute-api.us-east-1.amazonaws.com/dev/userInfo',{
			["id"]: this.state.email,["username"]: this.state.email, ["profile"]: this.state.profile})
			await Auth.signIn(this.state.email, this.state.password);

			this.props.userHasAuthenticated(true);
			this.props.history.push('/');
		} catch (e) {
			alert(e.message);
			this.setState({ isLoading: false });
		}
	};

	renderConfirmationForm() {
		return (
			<form onSubmit={this.handleConfirmationSubmit}>
				<FormGroup controlId="confirmationCode" bsSize="large">
					<ControlLabel>Confirmation Code</ControlLabel>
					<FormControl autoFocus type="tel" value={this.state.confirmationCode} onChange={this.handleChange} />
					<HelpBlock>Please check your email for the code.</HelpBlock>
				</FormGroup>
				<LoaderButton
					block
					bsSize="large"
					disabled={!this.validateConfirmationForm()}
					type="submit"
					isLoading={this.state.isLoading}
					text="Verify"
					loadingText="Verifying…"
				/>
			</form>
		);
	}

	renderForm() {
		return (
			<form onSubmit={this.handleSubmit}>
				<FormGroup controlId="email" bsSize="large">
					<ControlLabel>Email</ControlLabel>
					<FormControl autoFocus type="email" value={this.state.email} onChange={this.handleChange} />
				</FormGroup>
				<FormGroup controlId="password" bsSize="large">
					<ControlLabel>Password</ControlLabel>
					<FormControl value={this.state.password} onChange={this.handleChange} type="password" />
				</FormGroup>
				<FormGroup controlId="confirmPassword" bsSize="large">
					<ControlLabel>Confirm Password</ControlLabel>
					<FormControl value={this.state.confirmPassword} onChange={this.handleChange} type="password" />
				</FormGroup>
				<FormGroup controlId="profileType" bsSize="large">
					<ControlLabel>Profile Type</ControlLabel>
					<FormControl componentClass="select" value={this.state.profile} onChange={this.handleProfileChange}>
						<option value="default" hidden>Select a profile</option>
						<option value="Candidate">Candidate</option>
						<option value="Recruiter">Recruiter</option>
					</FormControl>
				</FormGroup>
				<LoaderButton
					block
					bsSize="large"
					disabled={!this.validateForm()}
					type="submit"
					isLoading={this.state.isLoading}
					text="Signup"
					loadingText="Signing up…"
				/>
			</form>
		);
	}

	render() {
		return (
			<div className="Signup">{this.state.newUser === null ? this.renderForm() : this.renderConfirmationForm()}</div>
		);
	}
}
