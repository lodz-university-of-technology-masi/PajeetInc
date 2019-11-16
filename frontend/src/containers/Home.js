import React, { Component } from 'react';
import { PageHeader, ListGroup } from 'react-bootstrap';
import { API } from 'aws-amplify';
import './Home.css';

export default class Home extends Component {
	constructor(props) {
		super(props);

		this.state = {
			isLoading: true,
			testApiCall: []
		};
	}

	async componentDidMount() {
		if (!this.props.isAuthenticated) {
			return;
		}

		try {
			console.log('cos klika');
			const testApiCall = await this.testApiCall();
			this.setState({ testApiCall });
		} catch (e) {
			console.log('cos nie klika');
			alert(e);
		}

		this.setState({ isLoading: false });
	}

	testApiCall() {
		return API.get('dev', '/GetHello');
	}

	renderTestAPI(testApiCall) {
		console.log(testApiCall);
		console.log('test');
		return testApiCall.message;
	}

	renderLander() {
		return (
			<div className="lander">
				<h1>Test web app</h1>
				<p>A simple react test app</p>
			</div>
		);
	}

	renderTest() {
		return (
			<div className="test">
				<h1>Test API call</h1>
				<p>{!this.state.isLoading && this.renderTestAPI(this.state.testApiCall)}</p>
			</div>
		);
	}

	render() {
		return <div className="Home">{this.props.isAuthenticated ? this.renderTest() : this.renderLander()}</div>;
	}
}
