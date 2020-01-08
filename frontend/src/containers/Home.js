import React, { Component } from 'react';
//import { API } from 'aws-amplify';
import './Home.css';
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
			console.log(this.props.currentUser);
		} catch (e) {
			console.log('fetch exception');
			alert(e);
		}

		this.setState({ isLoading: false });
	}

	renderLander() {
		return (
			<div className="lander">
				<h1>HR Recruitment App</h1>
				<p>Recruit new staff</p>
			</div>
		);
	}

	renderLoggedHomePage() {
		return (
			<div className="LoggedHomePage">
				<p>{!this.state.isLoading}</p>
				<p>{this.props.currentUser && !this.state.isLoading && 'zalogowany'}</p>

			</div>
		);
	}

	render() {
		return <div className="Home">{this.props.isAuthenticated ? this.renderLoggedHomePage() : this.renderLander()}</div>;
	}
}
