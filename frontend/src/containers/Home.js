import React, { Component } from 'react';
import './Home.css';


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
		} catch (e) {
			alert(e);
		}

		this.setState({ isLoading: false });
	}

	renderLander() {
		return (
			<div className="lander">
				<h1>HR Recruitment App</h1>
				<p>Zatrudniaj Nowych Pracowników</p>
			</div>
		);
	}

	renderLoggedHomePage() {
		return (
			<div className="LoggedHomePage">
				<p>{!this.state.isLoading}</p>
				<div className="lander">
					<h1>HR Recruitment App</h1>
					<p>Zatrudniaj Nowych Pracowników</p>
					<h3>Witaj {localStorage.getItem('currentUsername')}</h3>
				</div>
			</div>
		);
	}

	render() {
		return <div className="Home">{this.props.isAuthenticated ? this.renderLoggedHomePage() : this.renderLander()}</div>;
	}
}
