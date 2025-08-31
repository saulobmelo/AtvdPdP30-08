// Smart Room Monitor - JavaScript Principal

// Configurações
const API_BASE = '/api';
const UPDATE_INTERVAL = 3000; // 3 segundos

// Estado da aplicação
let updateTimer = null;
let isLoading = false;

// Inicialização quando DOM estiver pronto
document.addEventListener('DOMContentLoaded', function() {
    console.log('Smart Room Monitor iniciado');
    
    // Carregar dados iniciais
    loadInitialData();
    
    // Configurar atualizações automáticas
    startAutoUpdate();
    
    // Configurar event listeners
    setupEventListeners();
});

// Carregar dados iniciais
async function loadInitialData() {
    try {
        await updateSensorData();
        await updateActionLog();
        updateSystemStatus('Sistema ativo e monitorando');
    } catch (error) {
        console.error('Erro ao carregar dados iniciais:', error);
        updateSystemStatus('Erro ao conectar com o sistema', 'danger');
    }
}

// Atualizar dados dos sensores
async function updateSensorData() {
    try {
        const response = await fetch(`${API_BASE}/data`);
        const data = await response.json();
        
        if (response.ok) {
            // Atualizar valores dos sensores
            updateElement('temp-value', `${data.temperatura}°C`);
            updateElement('presence-value', data.presenca ? 'Detectada' : 'Ausente');
            updateElement('light-value', `${data.luminosidade} lux`);
            
            // Atualizar status dos dispositivos
            updateDeviceStatus('light', data.lightStatus);
            updateDeviceStatus('fan', data.fanStatus);
            
            // Atualizar timestamp
            updateElement('last-update', data.timestamp);
            
            // Adicionar animação de atualização
            addUpdateAnimation();
            
        } else {
            throw new Error('Falha na resposta da API');
        }
    } catch (error) {
        console.error('Erro ao atualizar dados dos sensores:', error);
        updateSystemStatus('Erro ao atualizar dados', 'warning');
    }
}

// Atualizar log de ações
async function updateActionLog() {
    try {
        const response = await fetch(`${API_BASE}/actions`);
        const data = await response.json();
        
        if (response.ok && data.actions) {
            const logContainer = document.getElementById('action-log');
            
            if (data.actions.length === 0) {
                logContainer.innerHTML = '<p class="text-muted">Nenhuma ação registrada</p>';
                return;
            }
            
            // Criar HTML para as ações
            const actionsHtml = data.actions
                .slice(-10) // Últimas 10 ações
                .reverse()  // Mais recentes primeiro
                .map(action => `
                    <div class="action-item">
                        <i class='bx bx-check-circle text-success'></i>
                        ${action}
                    </div>
                `).join('');
            
            logContainer.innerHTML = actionsHtml;
        }
    } catch (error) {
        console.error('Erro ao atualizar log de ações:', error);
    }
}

// Controlar dispositivo
async function toggleDevice(device) {
    if (isLoading) return;
    
    try {
        isLoading = true;
        
        // Determinar ação baseada no estado atual
        const currentStatus = getDeviceCurrentStatus(device);
        const action = currentStatus ? 'desligar' : 'ligar';
        
        // Mostrar loading no botão
        setButtonLoading(device, true);
        
        const response = await fetch(`${API_BASE}/devices`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                device: device,
                action: action
            })
        });
        
        const result = await response.json();
        
        if (response.ok && result.success) {
            // Atualizar interface imediatamente
            updateDeviceStatus(device === 'luz' ? 'light' : 'fan', !currentStatus);
            
            // Atualizar log de ações
            setTimeout(updateActionLog, 500);
            
            // Mostrar feedback
            showNotification(result.message, 'success');
            
        } else {
            throw new Error(result.error || result.message || 'Erro ao controlar dispositivo');
        }
        
    } catch (error) {
        console.error('Erro ao controlar dispositivo:', error);
        showNotification(`Erro ao controlar ${device}: ${error.message}`, 'danger');
    } finally {
        isLoading = false;
        setButtonLoading(device, false);
    }
}

// Obter status atual do dispositivo
function getDeviceCurrentStatus(device) {
    const statusElement = document.getElementById(device === 'luz' ? 'light-status' : 'fan-status');
    return statusElement && statusElement.textContent.includes('Ligad');
}

// Atualizar status do dispositivo na interface
function updateDeviceStatus(deviceType, isOn) {
    const statusId = `${deviceType}-status`;
    const btnId = `${deviceType}-btn`;
    
    const statusElement = document.getElementById(statusId);
    const btnElement = document.getElementById(btnId);
    
    if (statusElement) {
        statusElement.textContent = isOn ? 'Ligado' : 'Desligado';
        statusElement.className = `font-weight-bold ${isOn ? 'text-success' : 'text-secondary'}`;
    }
    
    if (btnElement) {
        btnElement.className = `btn ${isOn ? 'device-on' : 'device-off'}`;
    }
}

// Configurar loading no botão
function setButtonLoading(device, loading) {
    const btnId = device === 'luz' ? 'light-btn' : 'fan-btn';
    const btnElement = document.getElementById(btnId);
    
    if (btnElement) {
        if (loading) {
            btnElement.classList.add('loading');
            btnElement.disabled = true;
        } else {
            btnElement.classList.remove('loading');
            btnElement.disabled = false;
        }
    }
}

// Atualizar elemento do DOM
function updateElement(elementId, value) {
    const element = document.getElementById(elementId);
    if (element) {
        element.textContent = value;
    }
}

// Atualizar status do sistema
function updateSystemStatus(message, type = 'info') {
    const statusElement = document.getElementById('system-status');
    if (statusElement) {
        statusElement.textContent = message;
        
        // Atualizar classe do alert pai
        const alertElement = statusElement.closest('.alert');
        if (alertElement) {
            alertElement.className = `alert alert-${type}`;
        }
    }
}

// Adicionar animação de atualização
function addUpdateAnimation() {
    const cards = document.querySelectorAll('.sensor-card');
    cards.forEach(card => {
        card.classList.add('updated');
        setTimeout(() => {
            card.classList.remove('updated');
        }, 500);
    });
}

// Mostrar notificação
function showNotification(message, type = 'info') {
    // Criar elemento de notificação
    const notification = document.createElement('div');
    notification.className = `alert alert-${type} alert-dismissible fade show notification`;
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        z-index: 9999;
        min-width: 300px;
    `;
    
    notification.innerHTML = `
        ${message}
        <button type="button" class="close" data-dismiss="alert">
            <span>&times;</span>
        </button>
    `;
    
    // Adicionar ao DOM
    document.body.appendChild(notification);
    
    // Remover automaticamente após 3 segundos
    setTimeout(() => {
        if (notification.parentNode) {
            notification.remove();
        }
    }, 3000);
}

// Configurar atualizações automáticas
function startAutoUpdate() {
    if (updateTimer) {
        clearInterval(updateTimer);
    }
    
    updateTimer = setInterval(async () => {
        if (!isLoading) {
            await updateSensorData();
            await updateActionLog();
        }
    }, UPDATE_INTERVAL);
    
    console.log(`Atualizações automáticas iniciadas (${UPDATE_INTERVAL}ms)`);
}

// Parar atualizações automáticas
function stopAutoUpdate() {
    if (updateTimer) {
        clearInterval(updateTimer);
        updateTimer = null;
        console.log('Atualizações automáticas paradas');
    }
}

// Configurar event listeners
function setupEventListeners() {
    // Detectar quando a página perde/ganha foco
    document.addEventListener('visibilitychange', () => {
        if (document.hidden) {
            stopAutoUpdate();
        } else {
            startAutoUpdate();
            loadInitialData(); // Recarregar dados quando voltar à página
        }
    });
    
    // Detectar erros de rede
    window.addEventListener('online', () => {
        updateSystemStatus('Conexão restaurada', 'success');
        loadInitialData();
    });
    
    window.addEventListener('offline', () => {
        updateSystemStatus('Sem conexão com a internet', 'warning');
        stopAutoUpdate();
    });
}

// Funções globais para uso nos botões HTML
window.toggleDevice = toggleDevice;

// Log para debug
console.log('app.js carregado com sucesso');