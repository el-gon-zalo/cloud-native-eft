package cl.duoc.asyncorders.producer.config;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class RabbitMQConfig {

	private static final Logger log = LoggerFactory.getLogger(RabbitMQConfig.class);

	// Exchanges
	@Value("${rabbitmq.exchange.ventas}")
	private String exchangeVentas;

	@Value("${rabbitmq.exchange.notificaciones}")
	private String exchangeNotificaciones;

	@Value("${rabbitmq.exchange.dlx}")
	private String exchangeDlx;

	@Value("${rabbitmq.exchange.cursos}")
	private String exchangeCursos;

	// Queues
	@Value("${rabbitmq.queue.documentos}")
	private String queueDocumentos;

	@Value("${rabbitmq.queue.notificaciones}")
	private String queueNotificaciones;

	@Value("${rabbitmq.queue.documentos.dlq}")
	private String queueDocumentosDlq;

	@Value("${rabbitmq.queue.notificaciones.dlq}")
	private String queueNotificacionesDlq;

	@Value("${rabbitmq.queue.cursos}")
	private String queueCursos;

	@Value("${rabbitmq.queue.cursos.dlq}")
	private String queueCursosDlq;

	// Routing Keys
	@Value("${rabbitmq.routing.documento.generar}")
	private String routingDocumentoGenerar;

	@Value("${rabbitmq.routing.notificacion.enviar}")
	private String routingNotificacionEnviar;

	@Value("${rabbitmq.routing.error.documento}")
	private String routingErrorDocumento;

	@Value("${rabbitmq.routing.error.notificacion}")
	private String routingErrorNotificacion;

	@Value("${rabbitmq.routing.curso.documento.generar}")
	private String routingCursoDocumentoGenerar;

	@Value("${rabbitmq.routing.error.curso}")
	private String routingErrorCurso;

	// ==================== EXCHANGES ====================

	@Bean
	DirectExchange exchangeVentas() {

		return new DirectExchange(exchangeVentas);
	}

	@Bean
	DirectExchange exchangeNotificaciones() {

		return new DirectExchange(exchangeNotificaciones);
	}

	@Bean
	DirectExchange exchangeDlx() {

		return new DirectExchange(exchangeDlx);
	}

	@Bean
	DirectExchange exchangeCursos() {

		return new DirectExchange(exchangeCursos);
	}

	// ==================== QUEUES ====================

	@Bean
	Queue queueDocumentos() {

		Map<String, Object> args = new HashMap<>();
		args.put("x-dead-letter-exchange", exchangeDlx);
		args.put("x-dead-letter-routing-key", routingErrorDocumento);
		return new Queue(queueDocumentos, true, false, false, args);
	}

	@Bean
	Queue queueNotificaciones() {

		Map<String, Object> args = new HashMap<>();
		args.put("x-dead-letter-exchange", exchangeDlx);
		args.put("x-dead-letter-routing-key", routingErrorNotificacion);
		return new Queue(queueNotificaciones, true, false, false, args);
	}

	@Bean
	Queue queueDocumentosDlq() {

		return new Queue(queueDocumentosDlq, true);
	}

	@Bean
	Queue queueNotificacionesDlq() {

		return new Queue(queueNotificacionesDlq, true);
	}

	@Bean
	Queue queueCursos() {

		Map<String, Object> args = new HashMap<>();
		args.put("x-dead-letter-exchange", exchangeDlx);
		args.put("x-dead-letter-routing-key", routingErrorCurso);
		return new Queue(queueCursos, true, false, false, args);
	}

	@Bean
	Queue queueCursosDlq() {

		return new Queue(queueCursosDlq, true);
	}

	// ==================== BINDINGS ====================

	@Bean
	Binding bindingDocumentos() {

		return BindingBuilder.bind(queueDocumentos()).to(exchangeVentas()).with(routingDocumentoGenerar);
	}

	@Bean
	Binding bindingNotificaciones() {

		return BindingBuilder.bind(queueNotificaciones()).to(exchangeNotificaciones()).with(routingNotificacionEnviar);
	}

	@Bean
	Binding bindingDocumentosDlq() {

		return BindingBuilder.bind(queueDocumentosDlq()).to(exchangeDlx()).with(routingErrorDocumento);
	}

	@Bean
	Binding bindingNotificacionesDlq() {

		return BindingBuilder.bind(queueNotificacionesDlq()).to(exchangeDlx()).with(routingErrorNotificacion);
	}

	@Bean
	Binding bindingCursos() {

		return BindingBuilder.bind(queueCursos()).to(exchangeCursos()).with(routingCursoDocumentoGenerar);
	}

	@Bean
	Binding bindingCursosDlq() {

		return BindingBuilder.bind(queueCursosDlq()).to(exchangeDlx()).with(routingErrorCurso);
	}

	// ==================== MESSAGE CONVERTER ====================

	@Bean
	MessageConverter jsonMessageConverter() {

		return new Jackson2JsonMessageConverter();
	}

	@Bean
	RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {

		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(jsonMessageConverter());
		return rabbitTemplate;
	}

	// ==================== RABBIT ADMIN ====================

	@Bean
	RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {

		RabbitAdmin admin = new RabbitAdmin(connectionFactory);
		admin.setAutoStartup(true);
		return admin;
	}

	// ==================== INICIALIZACION ====================

	@EventListener(ApplicationReadyEvent.class)
	public void declareRabbitMQComponents(ApplicationReadyEvent event) {

		log.info("=== DECLARANDO COMPONENTES RABBITMQ ===");

		try {
			RabbitAdmin rabbitAdmin = event.getApplicationContext().getBean(RabbitAdmin.class);

			log.info("Declarando exchange: {}", exchangeVentas);
			rabbitAdmin.declareExchange(exchangeVentas());

			log.info("Declarando exchange: {}", exchangeNotificaciones);
			rabbitAdmin.declareExchange(exchangeNotificaciones());

			log.info("Declarando exchange: {}", exchangeDlx);
			rabbitAdmin.declareExchange(exchangeDlx());

			log.info("Declarando exchange: {}", exchangeCursos);
			rabbitAdmin.declareExchange(exchangeCursos());

			log.info("Declarando queue: {}", queueDocumentos);
			rabbitAdmin.declareQueue(queueDocumentos());

			log.info("Declarando queue: {}", queueNotificaciones);
			rabbitAdmin.declareQueue(queueNotificaciones());

			log.info("Declarando queue DLQ: {}", queueDocumentosDlq);
			rabbitAdmin.declareQueue(queueDocumentosDlq());

			log.info("Declarando queue DLQ: {}", queueNotificacionesDlq);
			rabbitAdmin.declareQueue(queueNotificacionesDlq());

			log.info("Declarando queue: {}", queueCursos);
			rabbitAdmin.declareQueue(queueCursos());

			log.info("Declarando queue DLQ: {}", queueCursosDlq);
			rabbitAdmin.declareQueue(queueCursosDlq());

			log.info("Declarando bindings...");
			rabbitAdmin.declareBinding(bindingDocumentos());
			rabbitAdmin.declareBinding(bindingNotificaciones());
			rabbitAdmin.declareBinding(bindingDocumentosDlq());
			rabbitAdmin.declareBinding(bindingNotificacionesDlq());
			rabbitAdmin.declareBinding(bindingCursos());
			rabbitAdmin.declareBinding(bindingCursosDlq());

			log.info("=== COMPONENTES RABBITMQ DECLARADOS EXITOSAMENTE ===");

		} catch (Exception e) {
			log.error("ERROR AL DECLARAR COMPONENTES RABBITMQ: {}", e.getMessage(), e);
		}
	}
}