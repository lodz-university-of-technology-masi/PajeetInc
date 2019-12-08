import React from 'react';
import { Route, Switch } from 'react-router-dom';
import AppliedRoute from './components/AppliedRoute';

import Home from './containers/Home';
import NotFound from './containers/NotFound';
import Login from './containers/Login';
import Signup from './containers/Signup';
import Tests from './containers/Tests'
import AddTests from './containers/AddTests'


export default ({ childProps }) => (
	<Switch>
		<AppliedRoute path="/" exact component={Home} props={childProps} />
		<AppliedRoute path="/login" exact component={Login} props={childProps} />
		<AppliedRoute path="/signup" exact component={Signup} props={childProps} />
		<AppliedRoute path="/tests" exact component={Tests} props={childProps} />
		<AppliedRoute path="/add_tests" exact component={AddTests} props={childProps} />

		{/* Finally, catch all unmatched routes */}
		<Route component={NotFound} />
	</Switch>
);
