import React, { useEffect, useState, useMemo } from 'react';
import { useParams, Link } from 'react-router-dom';
import { ArrowLeft, Calendar, Clock, Share2, Facebook, Twitter, Linkedin, Bookmark } from 'lucide-react';
import blogPosts from '../data/blogPosts.json';

export default function BlogPost() {
  const { id } = useParams();
  const [post, setPost] = useState(null);
  const [scrollProgress, setScrollProgress] = useState(0);

  useEffect(() => {
    const foundPost = blogPosts.find(p => p.slug === id);
    if (foundPost) {
      setPost(foundPost);
      document.title = `${foundPost.title} | Sarkari Exam AI`;
      
      // SEO: Meta Tags Injection
      const metaElements = {
        'description': foundPost.excerpt,
        'og:title': foundPost.title,
        'og:description': foundPost.excerpt,
        'og:image': foundPost.featuredImage,
        'og:type': 'article',
        'twitter:card': 'summary_large_image'
      };

      Object.entries(metaElements).forEach(([name, content]) => {
        let el = document.querySelector(`meta[name="${name}"], meta[property="${name}"]`);
        if (!el) {
          el = document.createElement('meta');
          if (name.startsWith('og:')) el.setAttribute('property', name);
          else el.setAttribute('name', name);
          document.head.appendChild(el);
        }
        el.setAttribute('content', content);
      });

      // JSON-LD Schema injection
      const schema = {
        "@context": "https://schema.org",
        "@type": "Article",
        "headline": foundPost.title,
        "description": foundPost.excerpt,
        "image": foundPost.featuredImage,
        "datePublished": foundPost.date,
        "author": { "@type": "Organization", "name": "Sarkari Exam AI" }
      };

      const script = document.createElement('script');
      script.type = 'application/ld+json';
      script.id = 'json-ld-schema';
      script.text = JSON.stringify(schema);
      document.head.appendChild(script);

      return () => {
        document.getElementById('json-ld-schema')?.remove();
      };
    }
  }, [id]);

  useEffect(() => {
    const handleScroll = () => {
      const totalScroll = document.documentElement.scrollHeight - window.innerHeight;
      const currentScroll = window.pageYOffset;
      setScrollProgress((currentScroll / totalScroll) * 100);
    };
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const relatedPosts = useMemo(() => {
    if (!post) return [];
    return blogPosts
      .filter(p => p.id !== post.id && p.tags?.some(tag => post.tags?.includes(tag)))
      .slice(0, 3);
  }, [post]);

  if (!post) return <div className="page-wrapper center">Post not found.</div>;

  return (
    <div className="page-wrapper animate-fadeIn">
      {/* Reading Progress Bar */}
      <div style={{ position: 'fixed', top: 0, left: 0, width: `${scrollProgress}%`, height: '4px', background: 'var(--primary)', zIndex: 1000, transition: 'width 0.2s ease' }} />

      <div className="page-with-sidebar">
        <div style={{ maxWidth: '850px', margin: '0 auto', position: 'relative' }}>
          
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '32px' }}>
            <Link to="/blog" className="btn btn-outline btn-sm">
              <ArrowLeft size={16} /> Back to Hub
            </Link>
            <div style={{ display: 'flex', gap: '8px' }}>
              <button className="btn btn-icon btn-sm"><Facebook size={16} /></button>
              <button className="btn btn-icon btn-sm"><Twitter size={16} /></button>
              <button className="btn btn-icon btn-sm"><Linkedin size={16} /></button>
            </div>
          </div>

          <article>
            <header style={{ marginBottom: '40px' }}>
              <div style={{ display: 'flex', gap: '8px', marginBottom: '16px' }}>
                {post.tags?.map(tag => (
                  <span key={tag} className="badge badge-primary">{tag}</span>
                ))}
              </div>
              <h1 style={{ fontSize: '3rem', lineHeight: 1.1, marginBottom: '24px', fontWeight: 800 }}>{post.title}</h1>
              <div style={{ display: 'flex', gap: '24px', color: 'var(--text-muted)', paddingBottom: '24px', borderBottom: '1px solid var(--border-color)' }}>
                <span className="flex-center gap-2"><Calendar size={18} /> {new Date(post.date).toLocaleDateString()}</span>
                <span className="flex-center gap-2"><Clock size={18} /> {post.readTime}</span>
              </div>
            </header>

            {post.featuredImage && (
              <div style={{ borderRadius: '24px', overflow: 'hidden', marginBottom: '48px', boxShadow: '0 20px 50px rgba(0,0,0,0.15)' }}>
                <img src={post.featuredImage} alt={post.title} style={{ width: '100%', height: '500px', objectFit: 'cover' }} />
              </div>
            )}

            <div 
              className="article-content" 
              style={{ fontSize: '1.2rem', lineHeight: 1.9, color: 'var(--text-primary)' }}
              dangerouslySetInnerHTML={{ __html: post.content }} 
            />

            {/* Source Information for SEO & Trust */}
            {post.sourceUrl && (
              <div style={{ marginTop: '40px', padding: '24px', background: 'var(--bg-glass)', border: '1px solid var(--border-color)', borderRadius: '16px', display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '16px' }}>
                <div>
                  <h4 style={{ margin: '0 0 4px 0', fontSize: '1rem', color: 'var(--text-primary)' }}>Fact Check & Original Source</h4>
                  <p style={{ margin: 0, fontSize: '0.9rem', color: 'var(--text-muted)' }}>This article is based on verified news from <strong>{post.sourceName || 'Official Agencies'}</strong>.</p>
                </div>
                <a href={post.sourceUrl} target="_blank" rel="noopener noreferrer" className="btn btn-sm btn-outline">
                  View Source Article
                </a>
              </div>
            )}

            {/* AI Schema FAQs Rendering */}
            {post.faqSchema && (
              <section style={{ marginTop: '60px', padding: '40px', background: 'var(--bg-tertiary)', borderRadius: '24px' }}>
                <h2 style={{ marginBottom: '32px' }}>Frequently Asked Questions</h2>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
                  {post.faqSchema.map((faq, i) => (
                    <div key={i} className="card" style={{ background: 'var(--bg-glass)' }}>
                      <h4 style={{ color: 'var(--primary)', marginBottom: '12px' }}>{faq.question}</h4>
                      <p style={{ margin: 0, fontSize: '1rem' }}>{faq.answer}</p>
                    </div>
                  ))}
                </div>
              </section>
            )}
          </article>

          {/* Related Posts */}
          {relatedPosts.length > 0 && (
            <section style={{ marginTop: '80px', paddingTop: '40px', borderTop: '2px solid var(--border-light)' }}>
              <h3 style={{ marginBottom: '32px' }}>Recommended for you</h3>
              <div className="grid-3">
                {relatedPosts.map(p => (
                  <Link to={`/blog/${p.slug}`} key={p.id} className="card no-padding" style={{ textDecoration: 'none' }}>
                    <img src={p.featuredImage} alt={p.title} style={{ width: '100%', height: '140px', objectFit: 'cover' }} />
                    <div style={{ padding: '16px' }}>
                      <h4 style={{ fontSize: '1rem', marginBottom: '8px' }}>{p.title}</h4>
                      <small className="text-muted">{p.readTime}</small>
                    </div>
                  </Link>
                ))}
              </div>
            </section>
          )}
        </div>
      </div>
      <style>{`
        .article-content h2 { margin-top: 2em; font-size: 2rem; color: var(--text-primary); }
        .article-content h3 { margin-top: 1.5em; font-size: 1.5rem; }
        .article-content p { margin-bottom: 1.5em; }
        .article-content ul { margin-bottom: 2em; padding-left: 1.5em; }
        .article-content li { margin-bottom: 0.8em; list-style: disc; }
        .flex-center { display: flex; align-items: center; }
        .gap-2 { gap: 8px; }
      `}</style>
    </div>
  );
}
