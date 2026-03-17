import React from 'react';
import {
    Container, Row, Col, Card, CardBody, Button, Input, Label,
    Modal, ModalHeader, ModalBody
} from 'reactstrap';
import { BarChart, Bar, Cell, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

const backgroundStyle = {
    backgroundColor: '#f4f6f8',
    minHeight: '100vh',
    width: '100%',
    paddingTop: '50px'
};

const CustomTooltip = ({ active, payload, label }) => {
    if (active && payload && payload.length) {
        const data = payload[0].payload;
        return (
            <div style={{
                backgroundColor: '#fff',
                padding: '10px',
                border: '1px solid #ccc',
                borderRadius: '5px',
                boxShadow: '0 2px 5px rgba(0,0,0,0.1)'
            }}>
                <p style={{ margin: 0, fontWeight: 'bold' }}>{`Hour: ${label}`}</p>
                <p style={{ margin: 0, color: '#2563eb' }}>
                    {`Consumption: ${data.consumption} kWh`}
                </p>
                {Number(data.consumption) > Number(data.maxAllowed) && (
                    <p style={{ margin: 0, color: '#dc2626', fontSize: '0.8rem', fontWeight: 'bold' }}>
                        (Exceeds limit of {data.maxAllowed}!)
                    </p>
                )}
            </div>
        );
    }
    return null;
};

class DeviceStatisticsPage extends React.Component {
    constructor(props) {
        super(props);

        const state = this.props.location.state || {};

        this.state = {
            deviceId: this.props.match.params.id,
            deviceName: state.deviceName || "Unknown Device",
            maxConsumption: Number(state.maxConsumption) || 10,

            selectedDate: new Date().toISOString().split('T')[0],
            measurements: [],
            chartData: [],
            isLoaded: false,

            showModal: false,
            modalType: 'INFO',
            alertDetails: { value: 0, hour: '' }
        };

        this.handleDateChange = this.handleDateChange.bind(this);
        this.toggleModal = this.toggleModal.bind(this);
        this.handleBarClick = this.handleBarClick.bind(this);
    }

    componentDidMount() {
        this.fetchMeasurements();
        this.interval = setInterval(() => {
            this.fetchMeasurements(true);
        }, 5000);
    }

    componentWillUnmount() {
        if (this.interval) clearInterval(this.interval);
    }

    toggleModal() {
        this.setState(prevState => ({
            showModal: !prevState.showModal
        }));
    }

    fetchMeasurements(isPolling = false) {
        const requestUrl = `http://localhost/monitoring/measurements/${this.state.deviceId}`;
        const token = localStorage.getItem("token");
        const headers = new Headers();
        headers.append('Accept', 'application/json');
        headers.append('Authorization', 'Bearer ' + token);

        fetch(requestUrl, { method: 'GET', headers: headers })
            .then(response => {
                if (response.status === 200) return response.json();
                return [];
            })
            .then(data => {
                this.setState({
                    measurements: data,
                    isLoaded: true
                }, () => {
                    this.filterDataForDate(this.state.selectedDate);
                });
            })
            .catch(err => {
                console.error(err);
                if (!isPolling) this.setState({ isLoaded: true });
            });
    }

    handleDateChange(e) {
        const newDate = e.target.value;
        this.setState({ selectedDate: newDate });
        this.filterDataForDate(newDate);
    }

    handleBarClick(entry) {
        const isOverLimit = Number(entry.consumption) > this.state.maxConsumption;

        this.setState({
            showModal: true,
            modalType: isOverLimit ? 'ALERT' : 'INFO',
            alertDetails: {
                value: entry.consumption,
                hour: entry.hour
            }
        });
    }

    filterDataForDate(dateString) {
        if (!this.state.measurements || this.state.measurements.length === 0) {
            this.setState({ chartData: [] });
            return;
        }

        const hourlyMap = new Array(24).fill(0);

        this.state.measurements.forEach(m => {
            let mDate;
            if (Array.isArray(m.timestamp)) {
                mDate = new Date(Date.UTC(
                    m.timestamp[0], m.timestamp[1] - 1, m.timestamp[2],
                    m.timestamp[3], m.timestamp[4], m.timestamp[5] || 0
                ));
            } else {
                mDate = new Date(m.timestamp);
            }

            const localYear = mDate.getFullYear();
            const localMonth = String(mDate.getMonth() + 1).padStart(2, '0');
            const localDay = String(mDate.getDate()).padStart(2, '0');
            const mDateString = `${localYear}-${localMonth}-${localDay}`;

            if (mDateString === dateString) {
                const hour = mDate.getHours();
                hourlyMap[hour] += m.hourlyConsumption;
            }
        });

        const chartData = hourlyMap.map((val, index) => ({
            hour: `${index}:00`,
            consumption: val,
            visualConsumption: val > this.state.maxConsumption ? this.state.maxConsumption : val,
            maxAllowed: this.state.maxConsumption
        }));

        this.setState({ chartData: chartData });
    }

    render() {
        const isAlert = this.state.modalType === 'ALERT';
        const modalColorClass = isAlert ? "text-danger" : "text-primary";
        const modalTitle = isAlert ? "⚠️ High Consumption Alert" : "ℹ️ Consumption Info";
        const yDomain = this.state.maxConsumption > 0 ? [0, this.state.maxConsumption] : ['auto', 'auto'];

        return (
            <div style={backgroundStyle}>
                <Container>
                    <Row className="mb-4">
                        <Col>
                            <Button
                                color="link"
                                onClick={() => this.props.history.push('/client')}
                                style={{ color: '#64748b', textDecoration: 'none', paddingLeft: 0, fontWeight: '600' }}
                            >
                                ← Back to Dashboard
                            </Button>

                            <h2 style={{ fontWeight: '700', color: '#1e293b', marginTop: '10px' }}>
                                Statistics for: <span style={{color: '#2563eb'}}>{this.state.deviceName}</span>
                            </h2>
                            <p className="text-muted">
                                Max allowed chart limit: <strong>{this.state.maxConsumption} kWh</strong>
                            </p>
                        </Col>
                    </Row>

                    <Row className="mb-4">
                        <Col md="4">
                            <Label style={{ fontWeight: '600', color: '#475569' }}>Select Date:</Label>
                            <Input
                                type="date"
                                value={this.state.selectedDate}
                                onChange={this.handleDateChange}
                                style={{ border: '1px solid #cbd5e1', borderRadius: '8px', padding: '10px' }}
                            />
                        </Col>
                    </Row>

                    <Row>
                        <Col md="12">
                            <Card style={{ border: 'none', borderRadius: '12px', boxShadow: '0 4px 15px rgba(0,0,0,0.05)' }}>
                                <CardBody style={{ height: '500px', padding: '30px' }}>
                                    {this.state.chartData.length > 0 ? (
                                        <ResponsiveContainer width="100%" height="100%">
                                            <BarChart
                                                data={this.state.chartData}
                                                margin={{ top: 20, right: 30, left: 20, bottom: 5 }}
                                            >
                                                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#e2e8f0" />
                                                <XAxis dataKey="hour" stroke="#64748b" tick={{fontSize: 12}} axisLine={false} tickLine={false} />

                                                <YAxis
                                                    domain={yDomain}
                                                    stroke="#64748b"
                                                    tick={{fontSize: 12}}
                                                    axisLine={false}
                                                    tickLine={false}
                                                    label={{ value: 'kWh', angle: -90, position: 'insideLeft', style: {textAnchor: 'middle', fill: '#94a3b8'} }}
                                                />

                                                <Tooltip content={<CustomTooltip />} cursor={{fill: '#f1f5f9'}} />

                                                <Bar dataKey="visualConsumption" radius={[4, 4, 0, 0]} barSize={40} name="Energy Consumption">
                                                    {this.state.chartData.map((entry, index) => (
                                                        <Cell
                                                            key={`cell-${index}`}
                                                            fill={Number(entry.consumption) > Number(this.state.maxConsumption) ? '#dc2626' : '#2563eb'}
                                                            onClick={() => this.handleBarClick(entry)}
                                                            style={{ cursor: 'pointer' }}
                                                        />
                                                    ))}
                                                </Bar>
                                            </BarChart>
                                        </ResponsiveContainer>
                                    ) : (
                                        <div className="d-flex justify-content-center align-items-center h-100">
                                            <h5 style={{color: '#94a3b8'}}>No data available for this date.</h5>
                                        </div>
                                    )}
                                </CardBody>
                            </Card>
                        </Col>
                    </Row>
                </Container>

                <Modal isOpen={this.state.showModal} toggle={this.toggleModal} centered>
                    <ModalHeader toggle={this.toggleModal} className={modalColorClass}>
                        {modalTitle}
                    </ModalHeader>
                    <ModalBody>
                        <p>
                            At <strong>{this.state.alertDetails.hour}</strong>, the recorded consumption was:
                        </p>
                        <hr />
                        <div className="d-flex justify-content-between align-items-center">

                            <div>
                                <small className="text-muted">Recorded Value</small>
                                <div style={{
                                    fontWeight: 'bold',
                                    fontSize: '1.4rem',
                                    color: isAlert ? '#dc2626' : '#2563eb'
                                }}>
                                    {this.state.alertDetails.value} kWh
                                </div>
                            </div>

                            <div className="text-end">
                                <small className="text-muted">Max Allowed</small>
                                <div style={{fontWeight: 'bold', fontSize: '1.1rem'}}>{this.state.maxConsumption} kWh</div>
                            </div>

                        </div>

                        {!isAlert && (
                            <div className="mt-3 text-center text-success bg-light p-2 rounded">
                                ✅ Consumption is within normal limits.
                            </div>
                        )}

                        {isAlert && (
                             <div className="mt-3 text-center text-danger bg-light p-2 rounded" style={{border: '1px solid #fca5a5'}}>
                                ⛔ <strong>CRITICAL:</strong> Consumption limit exceeded!
                            </div>
                        )}

                    </ModalBody>
                    {}
                </Modal>
            </div>
        );
    }
}

export default DeviceStatisticsPage;