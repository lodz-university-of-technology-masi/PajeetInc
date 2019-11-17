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
			console.log('props isnt authenticated')
			return;
		}

		try {
			console.log('componentDidMount() - try start');
			const testApiCall = await this.testApiCall();
			console.log('componentDidMount() - try middle');
			this.setState({ testApiCall });
			console.log('componentDidMount() - try end');
		} catch (e) {
			console.log('componentDidMount() - catch');
			alert(e);
		}

		this.setState({ isLoading: false });
	}

	testApiCall() {
		console.log('testApiCall()')
		return API.get('dev-back-end', '/forms');
	}

	renderTestAPI(testApiCall) {
		console.log('renderTestAPI()');
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
				<ListGroup>{!this.state.isLoading && this.renderTestAPI(this.state.testApiCall)}</ListGroup>
			</div>
		);
	}

	render() {
		return <div className="Home">{this.props.isAuthenticated ? this.renderTest() : this.renderLander()}</div>;
	}
}
