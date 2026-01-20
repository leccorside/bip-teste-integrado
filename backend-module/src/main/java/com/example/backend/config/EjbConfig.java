package com.example.backend.config;

import com.example.ejb.BeneficioEjbService;
import com.example.backend.model.Beneficio;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

/**
 * Configuração para expor EJBs como beans Spring.
 * Como estamos usando Spring Boot standalone (não em servidor Java EE),
 * precisamos criar beans Spring que encapsulam os EJBs e injetar o EntityManager do Spring.
 * 
 * IMPORTANTE: O BeneficioEjbService usa a entidade do EJB (com.example.ejb.model.Beneficio),
 * mas o EntityManager do Spring trabalha com a entidade do backend (com.example.backend.model.Beneficio).
 * Ambas mapeiam para a mesma tabela BENEFICIO. Criamos um wrapper que converte entre elas.
 */
@Configuration
public class EjbConfig {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Cria um bean Spring para o BeneficioEjbService.
     * Injeta um EntityManager wrapper que converte entre a entidade do EJB e a do backend.
     */
    @Bean
    @Transactional
    public BeneficioEjbService beneficioEjbService() {
        BeneficioEjbService ejbService = new BeneficioEjbService();
        // Cria um wrapper do EntityManager que converte entre as entidades
        EntityManager ejbEntityManager = createEjbEntityManagerWrapper(entityManager);
        
        // Usa reflection para injetar o EntityManager wrapper no EJB
        try {
            java.lang.reflect.Field emField = BeneficioEjbService.class.getDeclaredField("em");
            emField.setAccessible(true);
            // Remove a anotação @PersistenceContext se existir (não podemos fazer isso diretamente)
            // Mas podemos sobrescrever o valor do campo
            emField.set(ejbService, ejbEntityManager);
            
            // Verificar se foi injetado corretamente
            Object injectedEm = emField.get(ejbService);
            if (injectedEm == null || !java.lang.reflect.Proxy.isProxyClass(injectedEm.getClass())) {
                throw new RuntimeException("EntityManager wrapper não foi injetado corretamente");
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Erro ao injetar EntityManager no BeneficioEjbService", e);
        }
        return ejbService;
    }
    
    /**
     * Cria um wrapper do EntityManager usando Proxy do Java que converte
     * chamadas para a entidade do EJB em chamadas para a entidade do backend.
     */
    private EntityManager createEjbEntityManagerWrapper(EntityManager delegate) {
        return (EntityManager) java.lang.reflect.Proxy.newProxyInstance(
            EntityManager.class.getClassLoader(),
            new Class<?>[] { EntityManager.class },
            (proxy, method, args) -> {
                try {
                    String methodName = method.getName();
                    
                    // Interceptar find() - converter classe da entidade e resultado
                    if ("find".equals(methodName) && args != null && args.length >= 1) {
                        Class<?> entityClass = (Class<?>) args[0];
                        String className = entityClass.getName();
                        
                        if ("com.example.ejb.model.Beneficio".equals(className)) {
                            // Converter para a entidade do backend
                            Object[] newArgs = args.clone();
                            newArgs[0] = Beneficio.class;
                            
                            // Invocar find() com a entidade do backend
                            // Preserva LockModeType se presente (args[2])
                            Object result = method.invoke(delegate, newArgs);
                            
                            // Converter resultado de backend para EJB
                            if (result != null && result instanceof Beneficio) {
                                return convertBackendToEjb((Beneficio) result);
                            }
                            return result;
                        }
                    }
                    
                    // Interceptar merge() - converter entidade do EJB para backend
                    if ("merge".equals(methodName) && args != null && args.length >= 1) {
                        Object entity = args[0];
                        if (entity != null) {
                            String className = entity.getClass().getName();
                            if ("com.example.ejb.model.Beneficio".equals(className)) {
                                // Converter de EJB para backend antes do merge
                                Beneficio backendEntity = convertEjbToBackend((com.example.ejb.model.Beneficio) entity);
                                Object merged = method.invoke(delegate, backendEntity);
                                
                                // Converter resultado de volta para EJB
                                if (merged != null && merged instanceof Beneficio) {
                                    return convertBackendToEjb((Beneficio) merged);
                                }
                                return merged;
                            }
                        }
                    }
                    
                    // Para outros métodos, delegar diretamente
                    return method.invoke(delegate, args);
                } catch (Exception e) {
                    // Se houver erro relacionado à entidade do EJB, tentar converter
                    if (e.getCause() != null && e.getCause().getMessage() != null) {
                        String message = e.getCause().getMessage();
                        if (message.contains("com.example.ejb.model.Beneficio") && 
                            message.contains("Unable to locate entity descriptor")) {
                            // Tentar novamente com a entidade do backend
                            if ("find".equals(method.getName()) && args != null && args.length >= 1) {
                                Class<?> entityClass = (Class<?>) args[0];
                                if (entityClass.getName().equals("com.example.ejb.model.Beneficio")) {
                                    Object[] newArgs = args.clone();
                                    newArgs[0] = Beneficio.class;
                                    Object result = method.invoke(delegate, newArgs);
                                    if (result != null && result instanceof Beneficio) {
                                        return convertBackendToEjb((Beneficio) result);
                                    }
                                    return result;
                                }
                            }
                        }
                    }
                    throw e;
                }
            }
        );
    }
    
    /**
     * Converte uma entidade do backend para a entidade do EJB.
     */
    private com.example.ejb.model.Beneficio convertBackendToEjb(Beneficio backend) {
        com.example.ejb.model.Beneficio ejb = new com.example.ejb.model.Beneficio();
        ejb.setId(backend.getId());
        ejb.setNome(backend.getNome());
        ejb.setDescricao(backend.getDescricao());
        ejb.setValor(backend.getValor());
        ejb.setAtivo(backend.getAtivo());
        ejb.setVersion(backend.getVersion());
        return ejb;
    }
    
    /**
     * Converte uma entidade do EJB para a entidade do backend.
     */
    private Beneficio convertEjbToBackend(com.example.ejb.model.Beneficio ejb) {
        Beneficio backend = new Beneficio();
        backend.setId(ejb.getId());
        backend.setNome(ejb.getNome());
        backend.setDescricao(ejb.getDescricao());
        backend.setValor(ejb.getValor());
        backend.setAtivo(ejb.getAtivo());
        backend.setVersion(ejb.getVersion());
        return backend;
    }
}
