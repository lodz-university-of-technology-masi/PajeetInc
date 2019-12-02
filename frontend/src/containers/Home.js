import React, { Component } from 'react';
import { PageHeader, ListGroup } from 'react-bootstrap';
//import { API } from 'aws-amplify';
import './Home.css';
import config from '../config';
import { Auth } from 'aws-amplify';

export default class Home extends Component {
	constructor(props) {
		super(props);

		this.state = {
			isLoading: true,
			testApiCall: [],
			currentUser: null
		};
	}

	async componentDidMount() {
		if (!this.props.isAuthenticated) {
			return;
		}

		try {
			this.state.currentUser = await Auth.currentAuthenticatedUser();
			console.log(this.state.currentUser);
			fetch("/forms")
				.then(res => res.json())
				.then(json => this.setState({ testApiCall: json }));
		} catch (e) {
			console.log('fetch exception');
			alert(e);
		}

		this.setState({ isLoading: false });
	}

	renderTestAPI(testApiCall) {
		return testApiCall.message;
	}

	renderLander() {
		return (
			<div className="lander">
				<h1>HR Recruitment App</h1>
				<p>Recruit new staff</p>
			</div>
		);
	}

	renderTest() {
		return (
			<div className="test">
				<PageHeader>Test API call</PageHeader>
				<p>{!this.state.isLoading && this.renderTestAPI(this.state.testApiCall)}</p>
				<p>{this.props.isAuthenticated && !this.state.isLoading && this.state.currentUser.attributes.profile}</p>
			</div>
		);
	}

	render() {
		return <div className="Home">{this.props.isAuthenticated ? this.renderTest() : this.renderLander()}</div>;
	}
}
