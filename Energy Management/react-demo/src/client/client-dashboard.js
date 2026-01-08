import React from 'react';
import {
    Container,
    Card,
    CardBody,
    Col,
    Row,
    Badge,
    Input
} from 'reactstrap';
import * as API_CLIENT from "./api/client-api";


const backgroundStyle = {
    backgroundColor: '#f4f6f8',
    minHeight: '100vh',
    width: '100%',
    paddingBottom: '50px',
    paddingTop: '50px'
};

const cardStyle = {
    border: 'none',
    borderRadius: '12px',
    boxShadow: '0 10px 30px rgba(0, 0, 0, 0.05)',
    transition: 'transform 0.2s ease-in-out',
    height: '100%',
    overflow: 'hidden',
    backgroundColor: '#ffffff'
};

const labelStyle = {
    fontSize: '0.75rem',
    fontWeight: '700',
    textTransform: 'uppercase',
    letterSpacing: '0.8px',
    color: '#94a3b8',
    marginBottom: '5px'
};

const mainTitleStyle = {
    fontWeight: '700',
    color: '#1e293b',
    marginBottom: '5px'
};

const filterInputStyle = {
    border: '1px solid #e2e8f0',
    backgroundColor: '#ffffff',
    borderRadius: '8px',
    padding: '10px 15px',
    fontSize: '0.9rem',
    color: '#334155',
    boxShadow: '0 2px 5px rgba(0,0,0,0.02)'
};

const switchContainerStyle = {
    position: 'relative',
    display: 'inline-block',
    width: '40px',
    height: '22px',
    marginBottom: '0',
    marginRight: '12px',
    verticalAlign: 'middle'
};

const switchInputStyle = {
    opacity: 0,
    width: 0,
    height: 0
};

const sliderStyle = (isOn) => ({
    position: 'absolute',
    cursor: 'pointer',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: isOn ? '#2563eb' : '#e2e8f0',
    transition: '.3s',
    borderRadius: '34px'
});

const sliderBeforeStyle = (isOn) => ({
    position: 'absolute',
    content: '""',
    height: '16px',
    width: '16px',
    left: isOn ? '20px' : '4px',
    bottom: '3px',
    backgroundColor: 'white',
    transition: '.3s',
    borderRadius: '50%',
    boxShadow: '0 2px 4px rgba(0,0,0,0.2)'
});

class ClientDashboard extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            myDevices: [],
            username: localStorage.getItem("username") || "Client",
            isLoaded: false,
            searchQuery: '',
            sortBy: 'NAME_ASC'
        };
        this.handleLogout = this.handleLogout.bind(this);
        this.handleSearch = this.handleSearch.bind(this);
        this.handleSort = this.handleSort.bind(this);
        this.toggleDeviceStatus = this.toggleDeviceStatus.bind(this);
    }

    componentDidMount() {
        this.fetchMyDevices();
    }

    handleLogout() {
        localStorage.clear();
        window.location.href = "/login";
    }

    handleSearch(e) { this.setState({ searchQuery: e.target.value }); }
    handleSort(e) { this.setState({ sortBy: e.target.value }); }

    toggleDeviceStatus(id) {
        this.setState(prevState => ({
            myDevices: prevState.myDevices.map(device => {
                if (device.id === id) {
                    return { ...device, isActive: !device.isActive };
                }
                return device;
            })
        }));
    }

    fetchMyDevices() {
        const userId = localStorage.getItem("userId");
        if (!userId) { this.handleLogout(); return; }

        API_CLIENT.getMyDevices((result, status, err) => {
            if (status === 200 && result) {
                const devicesWithState = result.map(d => ({ ...d, isActive: true }));
                this.setState({ myDevices: devicesWithState, isLoaded: true });
            } else if (status === 401 || status === 403) {
                this.handleLogout();
            } else {
                this.setState({ isLoaded: true });
            }
        });
    }

    render() {
        let processedDevices = this.state.myDevices.filter(device =>
            device.name.toLowerCase().includes(this.state.searchQuery.toLowerCase())
        );

        processedDevices.sort((a, b) => {
            if (this.state.sortBy === 'NAME_ASC') return a.name.localeCompare(b.name);
            if (this.state.sortBy === 'NAME_DESC') return b.name.localeCompare(a.name);
            if (this.state.sortBy === 'CONS_ASC') return a.max_consumption - b.max_consumption;
            if (this.state.sortBy === 'CONS_DESC') return b.max_consumption - a.max_consumption;
            return 0;
        });

        const totalConsumption = processedDevices
            .filter(d => d.isActive)
            .reduce((acc, curr) => acc + Number(curr.max_consumption), 0);

        return (
            <div style={backgroundStyle}>
                <Container>

                    {/* Header + Stats */}
                    <Row className="mb-5 align-items-center">
                        <Col md="6">
                            <h2 style={mainTitleStyle}>My Smart Devices</h2>
                            <p style={{ color: '#64748b', margin: 0 }}>
                                Control your devices and monitor consumption.
                            </p>
                        </Col>
                        <Col md="6">
                             <div style={{
                                 backgroundColor: '#ffffff',
                                 padding: '20px',
                                 borderRadius: '12px',
                                 boxShadow: '0 4px 15px rgba(0,0,0,0.05)',
                                 display: 'flex',
                                 justifyContent: 'space-between',
                                 alignItems: 'center',
                                 borderLeft: '5px solid #2563eb'
                             }}>
                                 <div>
                                     <div style={labelStyle}>Current Active Load</div>
                                     <small className="text-muted">Total for ONLINE devices</small>
                                 </div>
                                 <div style={{ fontSize: '1.8rem', fontWeight: '800', color: '#1e293b' }}>
                                     {totalConsumption} <span style={{ fontSize: '1rem', color: '#64748b' }}>kWh</span>
                                 </div>
                             </div>
                        </Col>
                    </Row>

                    {/* Controls */}
                    <Row className="mb-4">
                        <Col md="8" className="mb-3 mb-md-0">
                            <Input type="text" placeholder="🔍 Search device by name..." value={this.state.searchQuery} onChange={this.handleSearch} style={filterInputStyle}/>
                        </Col>
                        <Col md="4">
                            <Input type="select" value={this.state.sortBy} onChange={this.handleSort} style={filterInputStyle}>
                                <option value="NAME_ASC">Name (A-Z)</option>
                                <option value="NAME_DESC">Name (Z-A)</option>
                                <option value="CONS_ASC">Consumption (Low to High)</option>
                                <option value="CONS_DESC">Consumption (High to Low)</option>
                            </Input>
                        </Col>
                    </Row>

                    {/* Grid Dispozitive */}
                    <Row>
                        {processedDevices.length > 0 ? (
                            processedDevices.map(device => (
                                <Col sm="12" md="6" lg="4" key={device.id} className="mb-4">
                                    <Card style={{
                                        ...cardStyle,
                                        opacity: device.isActive ? 1 : 0.7, // Mai transparent cand e OFF
                                        border: device.isActive ? 'none' : '1px solid #e2e8f0' // Bordura fina cand e OFF
                                    }}>
                                        <CardBody className="d-flex flex-column p-4">

                                            {/* HEADER: Nume */}
                                            <div className="mb-4">
                                                <h5 style={{ fontWeight: '700', color: '#1e293b', margin: 0, fontSize: '1.1rem' }}>
                                                    {device.name}
                                                </h5>
                                            </div>

                                            {/* ZONA DE CONTROL */}
                                            <div className="d-flex align-items-center mb-4 p-3 rounded" style={{ backgroundColor: '#f8fafc' }}>

                                                {/* 1. SWITCH */}
                                                <label style={switchContainerStyle}>
                                                    <input
                                                        type="checkbox"
                                                        style={switchInputStyle}
                                                        checked={device.isActive}
                                                        onChange={() => this.toggleDeviceStatus(device.id)}
                                                    />
                                                    <span style={sliderStyle(device.isActive)}></span>
                                                    <span style={{...sliderBeforeStyle(device.isActive)}}></span>
                                                </label>

                                                {/* 2. TEXT STATUS */}
                                                <span style={{
                                                    fontWeight: '700',
                                                    fontSize: '0.85rem',
                                                    color: device.isActive ? '#16a34a' : '#dc2626', // Verde sau Rosu la text
                                                    letterSpacing: '0.5px'
                                                }}>
                                                    {device.isActive ? 'DEVICE ONLINE' : 'DEVICE OFFLINE'}
                                                </span>
                                            </div>

                                            {/* Consum */}
                                            <div className="mt-auto">
                                                <div style={labelStyle}>Max Hourly Consumption</div>
                                                <div className="d-flex align-items-baseline">
                                                    <span style={{
                                                        fontSize: '2.5rem',
                                                        fontWeight: '800',
                                                        color: device.isActive ? '#2563eb' : '#94a3b8',
                                                        marginRight: '5px',
                                                        transition: 'color 0.3s'
                                                    }}>
                                                        {device.max_consumption}
                                                    </span>
                                                    <span style={{ color: '#64748b', fontWeight: '600', fontSize: '1.1rem' }}>kWh</span>
                                                </div>
                                            </div>

                                        </CardBody>
                                    </Card>
                                </Col>
                            ))
                        ) : (
                            <Col><div className="text-center p-5"><h4 style={{ color: '#94a3b8' }}>No devices found.</h4></div></Col>
                        )}
                    </Row>
                </Container>
            </div>
        );
    }
}

export default ClientDashboard;