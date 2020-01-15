import React from 'react';
import { Route, Switch } from 'react-router-dom';
import AppliedRoute from './components/AppliedRoute';

import Home from './containers/Home';
import NotFound from './containers/NotFound';
import Login from './containers/Login';
import Signup from './containers/Signup';
import ResetPassword from './containers/ResetPassword';
import Tests from './containers/Tests'
import AddTests from './containers/AddTests'
import AddCandidates from './containers/AddCandidates'
import UserTests from './containers/UserTests'
import ForcePasswordChange from './containers/ForcePasswordChange'
import CreateCandidateAccount from './containers/CreateCandidateAccount'
import ManageCandidates from './containers/ManageCandidates'


export default ({ childProps, profile, isAuthenticated }) => (
	<Switch>
		<AppliedRoute path="/" exact component={Home} props={childProps} />
		<AppliedRoute path="/login" exact component={Login} props={childProps} />
		<AppliedRoute path="/resetpassword" exact component={ResetPassword} props={childProps} />
		<AppliedRoute path="/signup" exact component={Signup} props={childProps} />
		{
			isAuthenticated && (
				<div>
				{profile == "Candidate" ? (
					<AppliedRoute path="/my_tests" exact component={UserTests} props={childProps} />
				) : 
				(
					<div>
						<AppliedRoute path="/my_tests" exact component={UserTests} props={childProps} />
						<AppliedRoute path="/add_tests" exact component={AddTests} props={childProps} />
						<AppliedRoute path="/add_candidates" exact component={AddCandidates} props={childProps} />
						<AppliedRoute path="/tests" exact component={Tests} props={childProps} />
						<AppliedRoute path="/createCandidateAccount" exact component={CreateCandidateAccount} props={childProps} />
						<AppliedRoute path="/manageCandidates" exact component={ManageCandidates} props={childProps} />
					</div>
				)
				}
				</div>
			)
		}
		<Route path='/forcePasswordChange' exact component={ForcePasswordChange} props={childProps} />
		{/* Finally, catch all unmatched routes */}
		<Route component={NotFound} />
	</Switch>
);
