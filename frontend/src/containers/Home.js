import React, { Component } from 'react';
import { PageHeader, ListGroup } from 'react-bootstrap';
//import { API } from 'aws-amplify';
import './Home.css';
import config from '../config';

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
			fetch(config.apiGateway.URL + "/forms")
				  .then(res => res.json())
				  .then(json => this.setState({ testApiCall: json }));
		} catch (e) {
			console.log('fetch exception');
			alert(e);
		}

		this.setState({ isLoading: false });
	}

	renderTestAPI(testApiCall) {
		console.log(testApiCall);
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
				<PageHeader>Test API call</PageHeader>
				<p>{!this.state.isLoading && this.renderTestAPI(this.state.testApiCall)}</p>
			</div>
		);
	}

	render() {
		return <div className="Home">{this.props.isAuthenticated ? this.renderTest() : this.renderLander()}</div>;
	}
}
