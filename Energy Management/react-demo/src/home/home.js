import React from 'react';
import {Container, Jumbotron, Button} from 'reactstrap';
// Daca folosim jwt-decode:
import {jwtDecode} from 'jwt-decode';

class Home extends React.Component {

    componentDidMount() {
        const token = localStorage.getItem("token");

        if (!token) {
            // Daca nu e logat, ramane pe Home cu mesaj de bun venit
            return;
        }

        // Incercam sa aflam rolul
        let role = "CLIENT"; // Default
        try {
            const decoded = jwtDecode(token);
            // Verifica cum se numeste campul in token-ul tau generat de Java
            // De multe ori e 'roles' sau 'authorities'
            if (decoded.roles && decoded.roles.includes("ADMIN")) {
                role = "ADMIN";
            }
            // SAU daca ai salvat rolul explicit la login in localStorage:
            // role = localStorage.getItem("role");
        } catch(e) {
            console.log("Token invalid");
        }

        // REDIRECTARE AUTOMATA
        if (role === 'ADMIN') {
            window.location.href = "/admin";
        } else {
            window.location.href = "/client";
        }
    }

    render() {
        return (
            <div>
                <Jumbotron fluid>
                    <Container fluid>
                        <h1 className="display-3">Energy Management System</h1>
                        <p className="lead">Bine ai venit!</p>
                        <hr className="my-2" />
                        <p>Te rugăm să te loghezi pentru a accesa platforma.</p>
                        <p className="lead">
                            <Button color="primary" href="/login">Login</Button>
                        </p>
                    </Container>
                </Jumbotron>
            </div>
        );
    }
}

export default Home;