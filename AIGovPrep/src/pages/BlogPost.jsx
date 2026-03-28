import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { ArrowLeft, Calendar, Clock } from 'lucide-react';
import blogPosts from '../data/blogPosts.json';

export default function BlogPost() {
  const { id } = useParams(); /* id matches slug */
  const [post, setPost] = useState(null);

  useEffect(() => {
    const foundPost = blogPosts.find(p => p.slug === id);
    if (foundPost) {
      setPost(foundPost);
      // Update basic Meta Tags for SEO temporarily
      document.title = `${foundPost.title} | Sarkari AI`;
      
      const metaDesc = document.querySelector('meta[name="description"]');
      if (metaDesc) metaDesc.setAttribute('content', foundPost.excerpt);
    }
  }, [id]);

  if (!post) {
    return (
      <div className="page-wrapper" style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '60vh' }}>
        <p>Post not found.</p>
      </div>
    );
  }

  return (
    <div className="page-wrapper animate-fadeIn">
      <div className="page-with-sidebar">
        <div style={{ maxWidth: '800px', margin: '0 auto' }}>
          
          <Link to="/blog" className="btn btn-outline" style={{ display: 'inline-flex', marginBottom: '32px' }}>
            <ArrowLeft size={16} style={{ marginRight: '8px' }} /> Back to Articles
          </Link>

          <article>
            <div style={{ marginBottom: '40px' }}>
              <div style={{ display: 'flex', gap: '8px', marginBottom: '20px', flexWrap: 'wrap' }}>
                {post.tags?.map(tag => (
                  <span key={tag} className="badge badge-primary" style={{ fontSize: '0.8rem' }}>{tag}</span>
                ))}
              </div>
              
              <h1 style={{ fontSize: '2.5rem', lineHeight: 1.2, marginBottom: '20px' }}>
                {post.title}
              </h1>
              
              <div style={{ display: 'flex', gap: '24px', color: 'var(--text-muted)', fontSize: '0.95rem', borderBottom: '1px solid var(--border-color)', paddingBottom: '24px' }}>
                <span style={{ display: 'flex', alignItems: 'center', gap: '6px' }}><Calendar size={16} /> {new Date(post.date).toLocaleDateString()}</span>
                <span style={{ display: 'flex', alignItems: 'center', gap: '6px' }}><Clock size={16} /> {post.readTime}</span>
              </div>
            </div>

            <div 
              className="article-content" 
              style={{ fontSize: '1.1rem', lineHeight: 1.8, color: 'var(--text-secondary)' }}
              dangerouslySetInnerHTML={{ __html: post.content }} 
            />
          </article>
        </div>
      </div>
      <style>{`
        /* Minimalist article styling to handle AI generated HTML elegantly */
        .article-content h2 { margin-top: 2.5em; margin-bottom: 1em; color: var(--text-primary); }
        .article-content h3 { margin-top: 2em; margin-bottom: 0.8em; color: var(--text-primary); }
        .article-content p { margin-bottom: 1.5em; }
        .article-content ul, .article-content ol { margin-bottom: 1.5em; margin-left: 1.5em; }
        .article-content li { margin-bottom: 0.5em; }
        .article-content strong { color: var(--text-primary); }
      `}</style>
    </div>
  );
}
